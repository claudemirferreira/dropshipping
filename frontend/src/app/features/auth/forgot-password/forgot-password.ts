import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardModule, InputTextModule, ButtonModule, MessageModule],
  template: `
    <div class="forgot-container">
      <p-card styleClass="forgot-card">
        <ng-template pTemplate="header">
          <div class="forgot-header">
            <img src="/assets/logo-email.jpeg" alt="Logo" class="brand-logo" />
            <h1>Recuperar acesso</h1>
            <p>Informe seu e-mail para receber a senha temporária</p>
          </div>
        </ng-template>

        @if (errorMessage()) {
          <p-message severity="error" [text]="errorMessage()!" />
        }
        @if (infoMessage()) {
          <p-message severity="info" [text]="infoMessage()!" />
        }
        @if (reason() === 'locked') {
          <p-message severity="warn">
            <span class="msg-text">
              Detectamos que sua conta pode estar bloqueada.<br />
              Você pode solicitar desbloqueio por senha temporária ou falar com a equipe.
            </span>
          </p-message>
        }

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="forgot-form">
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

          <p-button
            type="submit"
            label="Enviar"
            icon="pi pi-envelope"
            [loading]="loading()"
            [disabled]="form.invalid || loading()"
            styleClass="w-full"
          />

          <p-button
            type="button"
            label="Voltar ao login"
            icon="pi pi-sign-in"
            [text]="true"
            (onClick)="goToLogin()"
            styleClass="w-full"
          />
          
          <div class="contact-actions">
            <p>Precisa falar com a equipe?</p>
            <div class="contact-buttons">
              <a
                class="p-button p-button-text p-component w-full"
                [href]="mailtoHref"
                target="_blank"
              >
                <span class="p-button-icon pi pi-envelope"></span>
                <span class="p-button-label">Enviar e-mail</span>
              </a>
              <a
                class="p-button p-button-text p-component w-full"
                [href]="whatsHref"
                target="_blank"
              >
                <span class="p-button-icon pi pi-whatsapp"></span>
                <span class="p-button-label">WhatsApp</span>
              </a>
            </div>
          </div>
        </form>
      </p-card>
    </div>
  `,
  styles: [
    `
      .forgot-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 1rem;
        background: #f8fafc;
      }
      .forgot-card {
        width: 100%;
        max-width: 400px;
      }
      :host ::ng-deep .forgot-card,
      :host ::ng-deep .forgot-card .p-card {
        background: #ffffff !important;
      }
      :host ::ng-deep .forgot-card .p-card-body,
      :host ::ng-deep .forgot-card .p-card-content {
        background: #ffffff !important;
        color: #334155;
      }
      .forgot-header {
        padding: 2rem 2rem 1rem;
        text-align: center;
      }
      .forgot-header .brand-logo {
        height: 40px;
        margin-bottom: 0.5rem;
        object-fit: contain;
      }
      .forgot-header h1 {
        margin: 0;
        font-size: 1.5rem;
        color: var(--p-text-color);
      }
      .forgot-header p {
        margin: 0.5rem 0 0;
        color: var(--p-text-muted-color);
      }
      .forgot-form {
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
        padding: 0 2rem 2rem;
      }
      .field {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }
      .error {
        color: var(--p-red-500);
        font-size: 0.875rem;
      }
      :host ::ng-deep .p-message .p-message-text,
      .msg-text {
        white-space: normal;
        word-break: break-word;
      }
      .contact-actions {
        margin-top: 0.5rem;
        padding-top: 0.5rem;
        border-top: 1px dashed var(--p-content-border-color);
        color: var(--p-text-muted-color);
      }
      .contact-buttons {
        margin-top: 0.25rem;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.5rem;
      }
    `,
  ],
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  loading = this.auth.loading;
  errorMessage = this.auth.errorMessage;
  infoMessage = signal<string | null>(null);
  reason = signal<string | null>(null);
  mailtoHref = `mailto:${environment.supportEmail}?subject=Desbloqueio%20de%20conta&body=Olá%2C%20preciso%20de%20ajuda%20para%20desbloquear%20minha%20conta.`;
  whatsHref = `https://wa.me/${environment.supportWhatsapp?.replace(/\\D/g, '') || ''}?text=Desbloqueio%20de%20conta`;

  onSubmit(): void {
    if (this.form.invalid) return;
    const email = this.form.getRawValue().email;
    this.infoMessage.set(null);
    this.auth.forgotPassword(email).subscribe({
      next: () => this.infoMessage.set('Se a conta existir, enviamos a senha temporária.'),
      error: () => {},
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  ngOnInit(): void {
    const r = this.route.snapshot.queryParamMap.get('reason');
    this.reason.set(r);
  }
}
