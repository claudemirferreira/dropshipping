import { Component, inject, effect, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { LayoutService } from '../sakai/layout.service';
import { SakaiTopbarComponent } from '../sakai/sakai-topbar.component';
import { SakaiSidebarComponent } from '../sakai/sakai-sidebar.component';
import { SakaiFooterComponent } from '../sakai/sakai-footer.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    SakaiTopbarComponent,
    SakaiSidebarComponent,
    SakaiFooterComponent,
  ],
  template: `
<<<<<<< HEAD
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
            <img src="/assets/logo-email.jpeg" alt="Logo" class="brand-logo" />
            <span class="header-title">DropSeller</span>
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
=======
    <div
      class="layout-wrapper"
      [ngClass]="{
        'layout-static': layoutService.layoutConfig().menuMode === 'static',
        'layout-static-inactive':
          layoutService.layoutState().staticMenuDesktopInactive &&
          layoutService.layoutConfig().menuMode === 'static',
        'layout-mobile-active': layoutService.layoutState().mobileMenuActive
      }"
    >
      <app-sakai-topbar />
      <app-sakai-sidebar />
      <div class="layout-main-container">
        <main class="layout-main">
>>>>>>> refs/remotes/origin/develop
          <router-outlet />
        </main>
        <app-sakai-footer />
      </div>
    </div>
  `,
  styles: [
    `
      .layout-wrapper {
        height: 100vh;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        background: var(--app-surface-page);
      }

<<<<<<< HEAD
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

      .brand-logo {
        height: 28px;
        object-fit: contain;
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
=======
      .layout-main-container {
>>>>>>> refs/remotes/origin/develop
        flex: 1;
        min-height: 0;
        display: flex;
        flex-direction: column;
        margin-left: 0;
        transition: margin-left 0.3s ease;
      }

      .layout-main {
        flex: 1;
        min-height: 0;
        padding: 1.5rem 2rem;
        overflow: auto;
        background: var(--app-surface-page);
      }

      @media (min-width: 1200px) {
        .layout-main {
          padding: 1.75rem 2.5rem;
        }
      }

      @media (min-width: 992px) {
        .layout-wrapper.layout-static .layout-main-container {
          margin-left: 11rem;
        }

        .layout-wrapper.layout-static.layout-static-inactive .layout-main-container {
          margin-left: 0;
        }
      }

    `,
  ],
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  layoutService = inject(LayoutService);

  ngOnInit() {
    document.body.classList.add('layout-main-active');
  }

  ngOnDestroy() {
    document.body.classList.remove('layout-main-active');
  }

  constructor() {
    effect(() => {
      const state = this.layoutService.layoutState();
      if (state.mobileMenuActive) {
        document.body.classList.add('blocked-scroll');
      } else {
        document.body.classList.remove('blocked-scroll');
      }
    });
  }
}
