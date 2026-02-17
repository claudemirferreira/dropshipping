import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, finalize, catchError, throwError, switchMap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { Perfil } from './perfis.service';
import { UsersService } from './users.service';

export interface User {
  id: string;
  email: string;
  name: string;
  phone?: string;
  active: boolean;
  perfilCodes: string[];
  createdAt: string;
  updatedAt: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/api/v1/auth`;
  private readonly accessTokenKey = 'access_token';
  private readonly refreshTokenKey = 'refresh_token';
  private readonly userKey = 'current_user';
  private readonly userPerfisKey = 'user_perfis';
  private readonly rememberKey = 'remember_me';
  private readonly lastActivityKey = 'last_activity';

  private http = inject(HttpClient);
  private router = inject(Router);
  private usersService = inject(UsersService);

  private user = signal<User | null>(null);
  private userPerfis = signal<Perfil[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  accountLocked = signal<boolean>(false);
  lastErrorStatus = signal<number | null>(null);

  currentUser = computed(() => this.user());
  currentUserPerfis = computed(() => this.userPerfis());
  isLoggedIn = computed(() => !!this.user());

  constructor() {
    this.loadStoredUser();
    if (this.getAccessToken()) {
      this.usersService.getMyPerfis().subscribe({
        next: (perfis) => this.setUserPerfis(perfis),
        error: () => {
          // Mantém os perfis do storage em caso de falha
        },
      });
    }
    setInterval(() => this.checkIdleTimeout(), 60_000);
  }

  login(credentials: LoginRequest): Observable<TokenResponse> {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.accountLocked.set(false);
    this.lastErrorStatus.set(null);
    return this.http.post<TokenResponse>(`${this.api}/login`, credentials).pipe(
      tap((res) => {
        const remember = !!credentials.rememberMe;
        this.setRemember(remember);
        this.setTokens(res.accessToken, res.refreshToken, remember);
        this.fetchCurrentUser()
          .pipe(
            switchMap((user) =>
              this.usersService.getMyPerfis().pipe(
                tap((perfis) => this.setUserPerfis(perfis)),
                map(() => user)
              )
            )
          )
          .subscribe({
            next: (user) => {
              this.user.set(user);
              this.storage(remember).setItem(this.userKey, JSON.stringify(user));
            },
          });
        this.touchActivity();
      }),
      catchError((err) => {
        const msg =
          err.error?.message ?? 'E-mail ou senha inválidos. Tente novamente.';
        this.errorMessage.set(msg);
        this.lastErrorStatus.set(err.status ?? null);
        const lockedByStatus = err.status === 423;
        const lockedByMessage = /bloquead/i.test(msg) || /invalid[aá]?\s*3/i.test(msg);
        if (lockedByStatus || lockedByMessage) {
          this.accountLocked.set(true);
        }
        return throwError(() => err);
      }),
      finalize(() => this.loading.set(false))
    );
  }

  logout(): void {
    const token = this.getAccessToken();
    if (token) {
      this.http.post(`${this.api}/logout`, {}).subscribe();
    }
    this.clearSession();
    this.router.navigate(['/login']);
  }

  fetchCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.api}/me`).pipe(
      tap((user) => {
        this.user.set(user);
        this.storage(this.getRemember()).setItem(this.userKey, JSON.stringify(user));
        this.touchActivity();
      })
    );
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.accessTokenKey) ?? localStorage.getItem(this.accessTokenKey);
  }

  getRefreshToken(): string | null {
    return sessionStorage.getItem(this.refreshTokenKey) ?? localStorage.getItem(this.refreshTokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  setTokens(access: string, refresh: string, remember: boolean): void {
    const store = this.storage(remember);
    store.setItem(this.accessTokenKey, access);
    store.setItem(this.refreshTokenKey, refresh);
  }

  setRemember(remember: boolean): void {
    (remember ? localStorage : sessionStorage).setItem(this.rememberKey, String(remember));
    if (!remember) {
      localStorage.removeItem(this.accessTokenKey);
      localStorage.removeItem(this.refreshTokenKey);
      localStorage.removeItem(this.userKey);
      localStorage.removeItem(this.userPerfisKey);
    }
  }

  getRemember(): boolean {
    const val = localStorage.getItem(this.rememberKey) ?? sessionStorage.getItem(this.rememberKey);
    return val === 'true';
  }

  private storage(remember: boolean): Storage {
    return remember ? localStorage : sessionStorage;
  }

  touchActivity(): void {
    const store = this.storage(this.getRemember());
    store.setItem(this.lastActivityKey, String(Date.now()));
  }

  private checkIdleTimeout(): void {
    const idleMinutes = 30; // configurável: pode vir de environment
    const store = this.storage(this.getRemember());
    const last = Number(store.getItem(this.lastActivityKey) ?? '0');
    if (!last) return;
    const diffMin = (Date.now() - last) / 60000;
    if (diffMin > idleMinutes) {
      this.logout();
    }
  }

  forgotPassword(email: string): Observable<void> {
    this.loading.set(true);
    this.errorMessage.set(null);
    return this.http
      .post<void>(`${this.api}/forgot-password`, { email })
      .pipe(
        catchError((err) => {
          this.errorMessage.set(
            err.error?.message ?? 'Não foi possível solicitar a senha temporária.'
          );
          return throwError(() => err);
        }),
        finalize(() => this.loading.set(false))
      );
  }

  private loadStoredUser(): void {
    const store = this.storage(this.getRemember());
    const stored = store.getItem(this.userKey);
    if (stored) {
      try {
        this.user.set(JSON.parse(stored));
      } catch {
        this.clearSession();
      }
    }
    const storedPerfis = store.getItem(this.userPerfisKey);
    if (storedPerfis) {
      try {
        this.userPerfis.set(JSON.parse(storedPerfis));
      } catch {
        this.userPerfis.set([]);
      }
    }
  }

  private setUserPerfis(perfis: Perfil[]): void {
    this.userPerfis.set(perfis);
    this.storage(this.getRemember()).setItem(this.userPerfisKey, JSON.stringify(perfis));
  }

  private clearSession(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.userPerfisKey);
    sessionStorage.removeItem(this.accessTokenKey);
    sessionStorage.removeItem(this.refreshTokenKey);
    sessionStorage.removeItem(this.userKey);
    sessionStorage.removeItem(this.userPerfisKey);
    sessionStorage.removeItem(this.rememberKey);
    localStorage.removeItem(this.rememberKey);
    this.user.set(null);
    this.userPerfis.set([]);
  }
}
