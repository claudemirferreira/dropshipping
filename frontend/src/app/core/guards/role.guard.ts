import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de RBAC. Bloqueia acesso a rotas que exigem perfis específicos.
 *
 * Uso:
 *   { path: 'usuarios', canActivate: [authGuard, roleGuard], data: { roles: ['ADMIN'] } }
 *
 * Se o usuário não possuir nenhum dos perfis indicados em `data.roles`,
 * é redirecionado para `/unauthorized`.
 */
export const roleGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  _state: RouterStateSnapshot
) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const requiredRoles = (route.data?.['roles'] ?? []) as string[];
  if (!requiredRoles.length) {
    return true;
  }

  const userPerfilCodes = auth
    .currentUserPerfis()
    .map((p) => p.code?.toUpperCase())
    .filter((c): c is string => !!c);

  const hasAccess = requiredRoles.some((role) =>
    userPerfilCodes.includes(role.toUpperCase())
  );

  if (hasAccess) {
    return true;
  }

  router.navigate(['/unauthorized']);
  return false;
};
