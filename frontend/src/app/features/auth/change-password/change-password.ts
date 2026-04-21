import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PasswordModule } from 'primeng/password';
import { AuthService } from '../../../core/services/auth.service';
import { UsersService } from '../../../core/services/users.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PasswordModule,
  ],
  templateUrl: './change-password.html',
  styleUrl: './change-password.scss',
})
export class ChangePasswordComponent {
  private fb    = inject(FormBuilder);
  private users = inject(UsersService);
  private auth  = inject(AuthService);
  private router = inject(Router);

  loading  = signal(false);
  error    = signal<string | null>(null);
  needsChange = (): boolean => this.auth.needsPasswordChange();

  form = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword:     ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  });

  /** Requisitos da nova senha (reativos). */
  reqs = computed(() => {
    const pw = this.form.get('newPassword')?.value ?? '';
    return {
      length:  pw.length >= 8,
      upper:   /[A-Z]/.test(pw),
      lower:   /[a-z]/.test(pw),
      number:  /\d/.test(pw),
      special: /[@$!%*?&]/.test(pw),
    };
  });

  /** Nível de força da senha. */
  strength = computed((): 'weak' | 'medium' | 'strong' => {
    const met = Object.values(this.reqs()).filter(Boolean).length;
    if (met <= 2) return 'weak';
    if (met <= 4) return 'medium';
    return 'strong';
  });

  strengthLabel = computed(() => {
    const map = { weak: 'Fraca', medium: 'Média', strong: 'Forte' } as const;
    return map[this.strength()];
  });

  onSubmit(): void {
    if (this.form.invalid) return;

    const { currentPassword, newPassword, confirmPassword } = this.form.getRawValue();

    if (newPassword !== confirmPassword) {
      this.error.set('As senhas não coincidem.');
      return;
    }

    const user = this.auth['currentUser']?.();
    if (!user) {
      this.error.set('Sessão inválida. Faça login novamente.');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const isFirstLogin = this.needsChange();
    const obs = isFirstLogin
      ? this.auth.setPasswordFirstLogin(newPassword)
      : this.users.changePassword(user.id, { currentPassword, newPassword });

    obs.subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/password-reset-success']);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Erro ao alterar senha.');
        this.loading.set(false);
      },
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
