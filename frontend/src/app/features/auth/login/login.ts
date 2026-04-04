import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { CheckboxModule } from 'primeng/checkbox';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    CheckboxModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    rememberMe: [false],
  });

  loading = this.auth.loading;
  errorMessage = this.auth.errorMessage;
  infoMessage = signal<string | null>(null);
  locked = this.auth.accountLocked;

  onSubmit(): void {
    if (this.form.invalid) return;
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        const needsChange = this.auth.needsPasswordChange();
        this.router.navigate([needsChange ? '/change-password' : '/']);
      },
      error: () => { },
    });
  }

  onForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }

  onResolveIssue(): void {
    this.router.navigate(['/forgot-password'], { queryParams: { reason: 'locked' } });
  }
}
