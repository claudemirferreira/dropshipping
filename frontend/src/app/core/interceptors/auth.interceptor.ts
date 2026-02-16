import {
  HttpInterceptorFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const messageService = inject(MessageService);

  const token = auth.getAccessToken();
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        messageService.add({
          severity: 'warn',
          summary: 'Sessão expirada',
          detail: 'Faça login novamente para continuar.',
          life: 4000,
        });
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
