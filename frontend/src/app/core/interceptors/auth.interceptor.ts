import {
  HttpInterceptorFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';
import { HttpClient } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const messageService = inject(MessageService);
  const http = inject(HttpClient);

  const token = auth.getAccessToken();
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  auth['touchActivity']?.();

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        const refresh = auth.getRefreshToken();
        if (refresh) {
          return http
            .post<{ accessToken: string; refreshToken: string; tokenType: string; expiresIn: number }>(
              `/api/v1/auth/refresh`,
              { refreshToken: refresh }
            )
            .pipe(
              // Atualiza tokens e repete a requisição original
              catchError((refreshErr: HttpErrorResponse) => {
                messageService.add({
                  severity: 'warn',
                  summary: 'Sessão expirada',
                  detail: 'Faça login novamente para continuar.',
                  life: 4000,
                });
                auth.logout();
                router.navigate(['/login']);
                return throwError(() => refreshErr);
              }),
              // Reexecuta a requisição original com novo token
              switchMap((res) => {
                const remember = (auth as any).getRemember?.() ?? true;
                (auth as any).setTokens?.(res.accessToken, res.refreshToken, remember);
                const newReq = req.clone({
                  setHeaders: { Authorization: `Bearer ${res.accessToken}` },
                });
                return next(newReq);
              })
            );
        } else {
          messageService.add({
            severity: 'warn',
            summary: 'Sessão expirada',
            detail: 'Faça login novamente para continuar.',
            life: 4000,
          });
          auth.logout();
          router.navigate(['/login']);
        }
      }
      return throwError(() => err);
    })
  );
};
