import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    const needs = auth.needsPasswordChange();
    if (needs && state.url !== '/change-password') {
      router.navigate(['/change-password']);
      return false;
    }
    return true;
  }
  router.navigate(['/login']);
  return false;
};
