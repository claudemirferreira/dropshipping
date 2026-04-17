import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-password-reset-success',
  standalone: true,
  imports: [],
  templateUrl: './password-reset-success.html',
  styleUrl: './password-reset-success.scss',
})
export class PasswordResetSuccessComponent {
  private router = inject(Router);

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
