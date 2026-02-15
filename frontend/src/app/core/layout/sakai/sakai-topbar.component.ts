import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvatarModule } from 'primeng/avatar';
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
    AvatarModule,
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
        >
          <i class="pi pi-bars"></i>
        </button>
        <a routerLink="/" class="topbar-logo">
          <span class="topbar-logo-icon">📦</span>
          <span class="topbar-logo-text">Dropshipping</span>
        </a>
      </div>
      <div class="topbar-end">
        <button
          type="button"
          class="topbar-button"
          (click)="layoutService.toggleDarkMode()"
          [pTooltip]="layoutService.isDarkTheme() ? 'Modo claro' : 'Modo escuro'"
          tooltipPosition="bottom"
        >
          <i [class]="layoutService.isDarkTheme() ? 'pi pi-sun' : 'pi pi-moon'"></i>
        </button>
        <button
          type="button"
          class="topbar-button"
          (click)="refresh()"
          pTooltip="Atualizar"
          tooltipPosition="bottom"
        >
          <i class="pi pi-refresh"></i>
        </button>
        <button
          type="button"
          class="topbar-button user-button"
          (click)="userMenu.toggle($event)"
          [pTooltip]="currentUser()?.name ?? 'Usuário'"
          tooltipPosition="bottom"
        >
          <p-avatar
            [label]="currentUser()?.name?.charAt(0) ?? '?'"
            shape="circle"
            styleClass="topbar-avatar"
          />
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
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
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
      }

      .topbar-end {
        display: flex;
        align-items: center;
        gap: 0.125rem;
      }

      .topbar-button {
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

      .topbar-avatar {
        width: 2rem !important;
        height: 2rem !important;
        min-width: 2rem !important;
        min-height: 2rem !important;
        font-size: 0.8125rem !important;
        font-weight: 600 !important;
        background: transparent !important;
        color: rgba(255, 255, 255, 0.95) !important;
      }

      .user-button:hover .topbar-avatar {
        background: rgba(255, 255, 255, 0.12) !important;
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
