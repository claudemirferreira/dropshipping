import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPasswordComponent implements OnInit {
  private fb    = inject(FormBuilder);
  private auth  = inject(AuthService);
  private router = inject(Router);
  private route  = inject(ActivatedRoute);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  loading      = this.auth.loading;
  errorMessage = this.auth.errorMessage;
  infoMessage  = signal<string | null>(null);
  reason       = signal<string | null>(null);

  mailtoHref = `mailto:${environment.supportEmail}?subject=Desbloqueio%20de%20conta&body=Ol%C3%A1%2C%20preciso%20de%20ajuda%20para%20desbloquear%20minha%20conta.`;
  whatsHref  = `https://wa.me/${environment.supportWhatsapp?.replace(/\D/g, '') ?? ''}?text=Desbloqueio%20de%20conta`;

  ngOnInit(): void {
    const r = this.route.snapshot.queryParamMap.get('reason');
    this.reason.set(r);
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.infoMessage.set(null);
    const email = this.form.getRawValue().email;
    this.auth.forgotPassword(email).subscribe({
      next: () => this.infoMessage.set('E-mail enviado com sucesso! Verifique sua caixa de entrada.'),
      error: () => { },
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
