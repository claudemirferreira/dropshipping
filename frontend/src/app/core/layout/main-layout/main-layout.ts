import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { TooltipModule } from 'primeng/tooltip';
import { InputTextModule } from 'primeng/inputtext';
import { AuthService } from '../../services/auth.service';
import type { Rotina } from '../../services/rotinas.service';
import type { Perfil } from '../../services/perfis.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    ButtonModule,
    AvatarModule,
    TooltipModule,
    InputTextModule,
  ],
  template: `
    <div class="app-layout">
      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="sidebar-brand">
          <span class="brand-icon">📦</span>
        </div>
        <nav class="sidebar-nav">
          @for (group of menuGroups(); track group.perfil.code) {
            <div class="menu-group">
              <div class="menu-group-header">{{ group.perfil.code }}</div>
              @for (item of group.rotinas; track item.path) {
                <a
                  [routerLink]="item.path"
                  routerLinkActive="active"
                  [routerLinkActiveOptions]="item.path === '/dashboard' ? { exact: true } : { exact: false }"
                  class="nav-item nav-item-rotina"
                  [pTooltip]="item.name"
                  tooltipPosition="right"
                >
                  <i [class]="item.icon || 'pi pi-circle'"></i>
                  <span class="nav-item-label">{{ item.name }}</span>
                </a>
              }
            </div>
          }
        </nav>
        <div class="sidebar-footer">
          <div
            class="nav-item user-avatar"
            [pTooltip]="currentUser()?.name ?? 'Usuário'"
            tooltipPosition="right"
          >
            <p-avatar
              [label]="currentUser()?.name?.charAt(0) ?? '?'"
              shape="circle"
              styleClass="sidebar-avatar"
            />
          </div>
        </div>
      </aside>

      <!-- Main area -->
      <div class="main-wrapper">
        <!-- Header -->
        <header class="app-header">
          <div class="header-left">
            <span class="header-title">Dropshipping</span>
            <div class="header-search">
              <i class="pi pi-search search-icon"></i>
              <input
                type="text"
                pInputText
                placeholder="Buscar (Ctrl+K)"
                class="search-input"
                readonly
              />
            </div>
          </div>
          <div class="header-right header-buttons">
            <p-button
              icon="pi pi-refresh"
              [rounded]="true"
              [text]="true"
              size="small"
              (onClick)="refresh()"
              pTooltip="Atualizar"
            />
            <p-button
              icon="pi pi-sign-out"
              [rounded]="true"
              [text]="true"
              size="small"
              (onClick)="logout()"
              pTooltip="Sair"
            />
          </div>
        </header>

        <!-- Content -->
        <main class="app-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: [
    `
      .app-layout {
        display: flex;
        min-height: 100vh;
        background: #ffffff;
      }

      .sidebar {
        width: 14rem;
        min-width: 14rem;
        background: #f8fafc;
        border-right: 1px solid #e2e8f0;
        display: flex;
        flex-direction: column;
        padding: 1rem 0;
        overflow-y: auto;
      }

      .sidebar-brand {
        margin-bottom: 1.5rem;
        padding: 0 1rem;
        .brand-icon {
          font-size: 1.5rem;
        }
      }

      .sidebar-nav {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 0 0.5rem;
      }

      .menu-group {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
      }

      .menu-group-header {
        font-size: 0.6875rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        color: #94a3b8;
        padding: 0.5rem 0.75rem 0.25rem;
      }

      .nav-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.5rem 0.75rem;
        border-radius: var(--p-border-radius);
        color: #64748b;
        text-decoration: none;
        transition: all 0.2s;
        font-size: 0.875rem;
      }

      .nav-item i {
        flex-shrink: 0;
        width: 1.25rem;
        text-align: center;
      }

      .nav-item-label {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .nav-item:hover {
        background: #e2e8f0;
        color: #1e293b;
      }

      .nav-item.active {
        background: var(--sidebar-active-bg);
        color: var(--sidebar-active-text);
      }

      .sidebar-footer {
        padding: 1rem;
        border-top: 1px solid #e2e8f0;
      }

      .sidebar-avatar {
        width: 2rem !important;
        height: 2rem !important;
        font-size: 0.75rem !important;
      }

      .user-avatar {
        width: auto;
        padding: 0.25rem;
      }

      .main-wrapper {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-width: 0;
      }

      .app-header {
        height: 4rem;
        padding: 0 1.5rem;
        background: var(--header-bg);
        border-bottom: 1px solid #e2e8f0;
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      .header-left {
        display: flex;
        align-items: center;
        gap: 2rem;
      }

      .header-title {
        font-size: 1.25rem;
        font-weight: 700;
        color: var(--header-text);
      }

      .header-search {
        position: relative;
        display: flex;
        align-items: center;
      }

      .header-search .search-icon {
        position: absolute;
        left: 0.75rem;
        color: #94a3b8;
        font-size: 0.875rem;
      }

      .header-search .search-input {
        width: 16rem;
        padding: 0.5rem 0.75rem 0.5rem 2.25rem;
        border-radius: var(--p-border-radius);
        border: 1px solid #e2e8f0;
        font-size: 0.875rem;
        background: #ffffff;
        color: #1e293b;
      }

      .header-search .search-input::placeholder {
        color: #94a3b8;
      }

      .header-right {
        display: flex;
        align-items: center;
        gap: 0.25rem;
      }

      .header-buttons ::ng-deep .p-button {
        color: #64748b;
      }

      .header-buttons ::ng-deep .p-button:hover {
        color: #1e293b;
        background: #f1f5f9;
      }

      .app-content {
        flex: 1;
        padding: 1.5rem 2rem;
        overflow: auto;
        background: #ffffff;
      }

      @media (max-width: 768px) {
        .header-search {
          display: none;
        }
      }
    `,
  ],
})
export class MainLayoutComponent {
  private auth = inject(AuthService);
  currentUser = this.auth.currentUser;

  menuGroups = computed(() => {
    const perfis = [...this.auth.currentUserPerfis()].sort(
      (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
    );

    return perfis.map((perfil) => {
      const rotinasWithPath = (perfil.rotinas ?? [])
        .filter((r) => r.path?.trim() && r.active)
        .sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))
        .map((r) => ({
          path: r.path!,
          name: r.name,
          icon: r.icon || 'pi pi-circle',
        }));
      return {
        perfil: { code: perfil.code, name: perfil.name },
        rotinas: rotinasWithPath,
      };
    }).filter((g) => g.rotinas.length > 0);
  });

  logout(): void {
    this.auth.logout();
  }

  refresh(): void {
    window.location.reload();
  }
}
