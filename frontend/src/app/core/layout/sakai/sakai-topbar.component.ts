import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuModule } from 'primeng/menu';
import { TooltipModule } from 'primeng/tooltip';
import { LayoutService } from './layout.service';
import { AuthService } from '../../services/auth.service';
import type { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-sakai-topbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MenuModule,
    TooltipModule,
  ],
  template: `
    <div class="layout-topbar">
      <div class="topbar-start">
        <button
          type="button"
          class="topbar-menu-button"
          (click)="layoutService.onMenuToggle()"
          pTooltip="Menu"
          tooltipPosition="bottom"
          aria-label="Abrir ou fechar menu"
        >
          <i class="pi pi-bars" aria-hidden="true"></i>
        </button>
        <a routerLink="/" class="topbar-logo">
          <i class="pi pi-box topbar-logo-icon" aria-hidden="true"></i>
          <span class="topbar-logo-text">Dropshipping</span>
        </a>
      </div>
      <div class="topbar-end">
        <button
          type="button"
          class="topbar-button user-button"
          (click)="userMenu.toggle($event)"
          pTooltip="Clique para abrir o menu"
          tooltipPosition="bottom"
          [attr.aria-label]="'Menu do usuário ' + (currentUser()?.name ?? '')"
        >
          <span class="topbar-user-name">{{ currentUser()?.name ?? 'Usuário' }}</span>
        </button>
        <p-menu
          #userMenu
          [model]="userMenuItems"
          [popup]="true"
          styleClass="topbar-user-menu"
        />
      </div>
    </div>
  `,
  styles: [
    `
      .layout-topbar {
        height: 4rem;
        padding: 0 1.5rem;
        background: var(--layout-topbar-bg, var(--p-primary-color));
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
        display: flex;
        align-items: center;
        justify-content: space-between;
        position: sticky;
        top: 0;
        z-index: 1000;
      }

      .topbar-start {
        display: flex;
        align-items: center;
        gap: 0.5rem;
      }

      .topbar-menu-button {
        width: 2.5rem;
        height: 2.5rem;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: transparent;
        color: rgba(255, 255, 255, 0.9);
        transition: background-color 0.2s, color 0.2s;
        border: none;
        cursor: pointer;
      }

      .topbar-menu-button:hover {
        background: rgba(255, 255, 255, 0.12);
        color: #fff;
      }

      .topbar-menu-button i {
        font-size: 1.25rem;
      }

      .topbar-logo {
        display: flex;
        align-items: center;
        gap: 0.625rem;
        text-decoration: none;
        color: #fff;
        font-weight: 700;
        font-size: 1.375rem;
        letter-spacing: 0.02em;
      }

      .topbar-logo:hover {
        color: rgba(255, 255, 255, 0.95);
      }

      .topbar-logo-icon {
        font-size: 1.5rem;
        opacity: 0.95;
        color: rgba(255, 255, 255, 0.95);
      }

      .topbar-end {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding-left: 1rem;
        border-left: 1px solid rgba(255, 255, 255, 0.2);
      }

      .topbar-button {
        min-width: 2.5rem;
        height: 2.5rem;
        border-radius: 999px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 0 0.5rem;
        background: transparent;
        color: rgba(255, 255, 255, 0.9);
        transition: background-color 0.2s, color 0.2s;
        border: none;
        cursor: pointer;
      }

      .topbar-button:hover {
        background: rgba(255, 255, 255, 0.12);
        color: #fff;
      }

      .topbar-button i {
        font-size: 1.2rem;
      }

      .user-button {
        margin-left: 0.375rem;
      }

      .topbar-user-name {
        font-weight: 600;
        font-size: 0.9375rem;
        color: rgba(255, 255, 255, 0.95);
        max-width: 10rem;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      @media (max-width: 575px) {
        .topbar-user-name {
          display: none;
        }
      }
    `,
  ],
})
export class SakaiTopbarComponent {
  layoutService = inject(LayoutService);
  private auth = inject(AuthService);

  currentUser = this.auth.currentUser;

  userMenuItems: MenuItem[] = [
    {
      label: 'Sair',
      icon: 'pi pi-sign-out',
      command: () => this.auth.logout(),
    },
  ];

  refresh(): void {
    window.location.reload();
  }
}
