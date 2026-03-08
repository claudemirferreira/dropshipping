import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../../core/services/auth.service';
import { UsersService } from '../../../core/services/users.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    MessageModule,
  ],
  template: `
    <div class="change-container">
      <p-card class="change-card">
        <div class="header">
          <h1>Definir nova senha</h1>
          <p>Informe a senha temporária e defina sua nova senha.</p>
        </div>
        <form [formGroup]="form" class="form" (ngSubmit)="onSubmit()">
          <div class="field">
            <label>Senha atual</label>
            <input pPassword formControlName="currentPassword" [feedback]="false" class="w-full" />
          </div>
          <div class="field">
            <label>Nova senha</label>
            <input pPassword formControlName="newPassword" [feedback]="true" class="w-full" />
          </div>
          <div class="field">
            <label>Confirmar nova senha</label>
            <input pPassword formControlName="confirmPassword" [feedback]="false" class="w-full" />
          </div>
          <p-message *ngIf="error()" severity="error" [text]="error()!"></p-message>
          <button pButton type="submit" label="Alterar senha" class="w-full" [disabled]="loading()" > </button>
        </form>
      </p-card>
    </div>
  `,
  styles: [
    `
      .change-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1rem;
        background: var(--app-surface-page);
      }
      .change-card {
        width: 100%;
        max-width: 420px;
      }
      :host ::ng-deep .change-card,
      :host ::ng-deep .change-card .p-card {
        background: var(--app-surface-card) !important;
        border: 1px solid var(--app-border-color) !important;
        box-shadow: var(--app-shadow-card) !important;
      }
      .header {
        padding: 2rem 2rem 1rem;
      }
      .header h1 {
        margin: 0;
        font-size: 1.5rem;
        color: var(--app-text-primary);
      }
      .header p {
        margin: 0.5rem 0 0;
        color: var(--app-text-muted);
      }
      .form {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 0 2rem 2rem;
      }
      .field {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }
      /* Inputs alinhados com o tema (sem fundo preto) */
      :host ::ng-deep .p-inputtext,
      :host ::ng-deep .p-password input,
      :host ::ng-deep .p-password .p-inputtext {
        background-color: transparent !important;
        box-shadow: none !important;
        color: var(--app-text-primary) !important;
        caret-color: var(--p-primary-color) !important;
        -webkit-text-fill-color: var(--app-text-primary);
      }
      :host ::ng-deep .p-inputtext::placeholder,
      :host ::ng-deep .p-password input::placeholder,
      :host ::ng-deep .p-password .p-inputtext::placeholder {
        color: var(--app-text-muted) !important;
        opacity: 1;
      }
      :host ::ng-deep .p-inputtext:enabled:hover,
      :host ::ng-deep .p-inputtext:enabled:focus,
      :host ::ng-deep .p-password input:focus,
      :host ::ng-deep .p-password .p-inputtext:focus {
        background-color: transparent !important;
        box-shadow: none !important;
      }
      :host ::ng-deep input:-webkit-autofill,
      :host ::ng-deep input:-webkit-autofill:hover,
      :host ::ng-deep input:-webkit-autofill:focus {
        -webkit-text-fill-color: var(--app-text-primary);
        transition: background-color 5000s ease-in-out 0s;
        box-shadow: 0 0 0 1000px transparent inset !important;
        -webkit-box-shadow: 0 0 0 1000px transparent inset !important;
      }
    `,
  ],
})
export class ChangePasswordComponent {
  private fb = inject(FormBuilder);
  private users = inject(UsersService);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    const { currentPassword, newPassword, confirmPassword } = this.form.getRawValue();
    if (newPassword !== confirmPassword) {
      this.error.set('As senhas não coincidem.');
      return;
    }
    const user = this.auth['currentUser']();
    if (!user) {
      this.error.set('Sessão inválida.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const needs = this.auth.needsPasswordChange();
    const obs = needs
      ? this.auth.setPasswordFirstLogin(newPassword)
      : this.users.changePassword(user.id, { currentPassword, newPassword });

    obs.subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        const msg = err?.error?.message ?? 'Erro ao alterar senha.';
        this.error.set(msg);
        this.loading.set(false);
      },
    });
  }
}
