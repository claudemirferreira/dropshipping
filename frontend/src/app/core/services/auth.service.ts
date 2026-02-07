import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, finalize, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

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

  private user = signal<User | null>(null);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  currentUser = computed(() => this.user());
  isLoggedIn = computed(() => !!this.user());

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadStoredUser();
  }

  login(credentials: LoginRequest): Observable<TokenResponse> {
    this.loading.set(true);
    this.errorMessage.set(null);
    return this.http.post<TokenResponse>(`${this.api}/login`, credentials).pipe(
      tap((res) => {
        this.setTokens(res.accessToken, res.refreshToken);
        this.fetchCurrentUser().subscribe();
      }),
      catchError((err) => {
        this.errorMessage.set(
          err.error?.message ?? 'E-mail ou senha inválidos. Tente novamente.'
        );
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

  private loadStoredUser(): void {
    const stored = localStorage.getItem(this.userKey);
    if (stored) {
      try {
        this.user.set(JSON.parse(stored));
      } catch {
        this.clearSession();
      }
    }
  }

  private clearSession(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userKey);
    this.user.set(null);
  }
}
