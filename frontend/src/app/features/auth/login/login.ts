import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    CardModule,
    MessageModule,
  ],
  template: `
    <div class="login-container">
      <p-card styleClass="login-card">
        <ng-template pTemplate="header">
          <div class="login-header">
            <h1>Dropshipping</h1>
            <p>Entre com suas credenciais</p>
          </div>
        </ng-template>

        @if (errorMessage()) {
          <p-message severity="error" [text]="errorMessage()!" />
        }

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="login-form">
          <div class="field">
            <label for="email">E-mail</label>
            <input
              id="email"
              pInputText
              type="email"
              formControlName="email"
              placeholder="seu@email.com"
              autocomplete="email"
            />
            @if (form.get('email')?.invalid && form.get('email')?.touched) {
              <small class="error">E-mail é obrigatório</small>
            }
          </div>

          <div class="field">
            <label for="password">Senha</label>
            <p-password
              id="password"
              formControlName="password"
              placeholder="Sua senha"
              [feedback]="false"
              [toggleMask]="true"
              inputStyleClass="w-full"
            />
            @if (form.get('password')?.invalid && form.get('password')?.touched) {
              <small class="error">Senha é obrigatória</small>
            }
          </div>

          <p-button
            type="submit"
            label="Entrar"
            icon="pi pi-sign-in"
            [loading]="loading()"
            [disabled]="form.invalid || loading()"
            styleClass="w-full"
          />
        </form>
      </p-card>
    </div>
  `,
  styles: [
    `
      .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1rem;
        background: var(--app-surface-page);
      }

      .login-card {
        width: 100%;
        max-width: 400px;
      }

      :host ::ng-deep .login-card,
      :host ::ng-deep .login-card .p-card {
        background: var(--app-surface-card) !important;
        border: 1px solid var(--app-border-color) !important;
        box-shadow: var(--app-shadow-card) !important;
      }

      :host ::ng-deep .login-card .p-card-body,
      :host ::ng-deep .login-card .p-card-content {
        background: var(--app-surface-card) !important;
        color: var(--app-text-secondary);
      }

      .login-header {
        padding: 2rem 2rem 1rem;
        text-align: center;
        h1 {
          margin: 0;
          font-size: 1.75rem;
          color: var(--app-text-primary);
        }
        p {
          margin: 0.5rem 0 0;
          color: var(--app-text-muted);
        }
      }

      .login-form {
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
        padding: 0 2rem 2rem;
      }

      .field {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;

        label {
          font-weight: 500;
          color: var(--app-text-secondary);
        }
      }

      .error {
        color: var(--p-red-500, #ef4444);
        font-size: 0.875rem;
      }

      :host ::ng-deep .p-password-input {
        width: 100%;
      }
    `,
  ],
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  loading = this.auth.loading;
  errorMessage = this.auth.errorMessage;

  onSubmit(): void {
    if (this.form.invalid) return;

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {},
    });
  }
}
