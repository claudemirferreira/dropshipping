import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, CardModule],
  template: `
    <div class="page-header">
      <div class="page-title-block">
        <h1 class="page-title">Dashboard</h1>
        <p class="page-description">Visão geral do sistema</p>
      </div>
      <div class="page-badge">
        <span class="badge-dot"></span>
        <span class="badge-value">{{ (currentUser()?.perfilCodes ?? [])[0] || '—' }}</span>
        <span class="badge-label">seu perfil</span>
      </div>
    </div>

    <div class="dashboard-cards">
      <p-card styleClass="welcome-card">
        <ng-template pTemplate="header">
          <div class="card-header">
            <h2>Bem-vindo, {{ currentUser()?.name }}!</h2>
          </div>
        </ng-template>
        <p>
          Você está conectado ao sistema de Dropshipping. Use o menu lateral para
          navegar entre as funcionalidades disponíveis.
        </p>
        <ng-template pTemplate="footer">
          <small class="card-footer-text">E-mail: {{ currentUser()?.email }}</small>
        </ng-template>
      </p-card>
    </div>
  `,
  styles: [
    `
      .dashboard-cards {
        display: grid;
        gap: 1.5rem;
      }

      :host ::ng-deep .welcome-card,
      :host ::ng-deep .welcome-card .p-card {
        background: var(--app-surface-card) !important;
        border-radius: var(--p-border-radius);
        border: 1px solid var(--app-border-color);
        box-shadow: var(--app-shadow-card);
      }

      :host ::ng-deep .welcome-card .p-card-body,
      :host ::ng-deep .welcome-card .p-card-content {
        background: #ffffff !important;
        color: #334155;
      }

      .card-header h2 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: #1e293b;
      }

      .card-footer-text {
        color: #64748b;
        font-size: 0.875rem;
      }
    `,
  ],
})
export class DashboardComponent {
  private auth = inject(AuthService);
  currentUser = this.auth.currentUser;
}
