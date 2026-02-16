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
  profile: 'ADMIN' | 'MANAGER' | 'SELLER' | 'OPERATOR';
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
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/api/v1/auth`;
  private readonly accessTokenKey = 'access_token';
  private readonly refreshTokenKey = 'refresh_token';
  private readonly userKey = 'current_user';
  private readonly userPerfisKey = 'user_perfis';

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
    if (this.getAccessToken() && this.user()) {
      this.usersService.getMyPerfis().subscribe({
        next: (perfis) => this.setUserPerfis(perfis),
        error: () => {
          // Mantém os perfis do localStorage em caso de falha (ex: token expirado ainda não tratado)
          // Não limpar para evitar menu vazio até o refresh do token
        },
      });
    }
  }

  login(credentials: LoginRequest): Observable<TokenResponse> {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.accountLocked.set(false);
    this.lastErrorStatus.set(null);
    return this.http.post<TokenResponse>(`${this.api}/login`, credentials).pipe(
      tap((res) => {
        this.setTokens(res.accessToken, res.refreshToken);
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
              localStorage.setItem(this.userKey, JSON.stringify(user));
            },
          });
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
        localStorage.setItem(this.userKey, JSON.stringify(user));
      })
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  private setTokens(access: string, refresh: string): void {
    localStorage.setItem(this.accessTokenKey, access);
    localStorage.setItem(this.refreshTokenKey, refresh);
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
    const stored = localStorage.getItem(this.userKey);
    if (stored) {
      try {
        this.user.set(JSON.parse(stored));
      } catch {
        this.clearSession();
      }
    }
    const storedPerfis = localStorage.getItem(this.userPerfisKey);
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
    localStorage.setItem(this.userPerfisKey, JSON.stringify(perfis));
  }

  private clearSession(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userKey);
    localStorage.removeItem(this.userPerfisKey);
    this.user.set(null);
    this.userPerfis.set([]);
  }
}
