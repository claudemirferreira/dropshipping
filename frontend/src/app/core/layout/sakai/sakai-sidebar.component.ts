import { Component, inject, computed, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { filter, Subject, takeUntil } from 'rxjs';
import { TooltipModule } from 'primeng/tooltip';
import { LayoutService } from './layout.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-sakai-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, TooltipModule],
  template: `
    <aside
      class="layout-sidebar"
      [class.layout-sidebar-active]="layoutService.isSidebarActive()"
      [class.layout-sidebar-static-inactive]="
        layoutService.layoutState().staticMenuDesktopInactive &&
        layoutService.layoutConfig().menuMode === 'static'
      "
    >
      <div class="sidebar-header">
        <span class="sidebar-brand-icon">📦</span>
      </div>
      <nav class="sidebar-nav">
        @for (group of menuGroups(); track group.perfil.code) {
          <div class="menu-group">
            <div class="menu-group-header">{{ group.perfil.code }}</div>
            @for (item of group.rotinas; track item.path + item.name) {
              <a
                [routerLink]="item.path"
                routerLinkActive="active-menuitem"
                [routerLinkActiveOptions]="
                  item.path === '/dashboard' ? { exact: true } : { exact: false }
                "
                class="menuitem-link"
                [pTooltip]="item.name"
                tooltipPosition="right"
                (click)="onItemClick()"
              >
                <i [class]="item.icon || 'pi pi-circle'"></i>
                <span class="menuitem-text">{{ item.name }}</span>
              </a>
            }
          </div>
        }
      </nav>
    </aside>
    <div
      class="layout-sidebar-mask"
      [class.layout-sidebar-mask-active]="layoutService.isSidebarActive()"
      (click)="layoutService.closeMenus()"
      role="button"
      tabindex="-1"
      aria-label="Fechar menu"
    ></div>
  `,
  styles: [
    `
      .layout-sidebar {
        position: fixed;
        left: 0;
        top: 0;
        height: 100vh;
        width: 17rem;
        background: var(--p-surface-50, #f8fafc);
        border-right: 1px solid var(--p-surface-200, #e2e8f0);
        display: flex;
        flex-direction: column;
        z-index: 999;
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        overflow-y: auto;
        box-shadow: 2px 0 8px rgba(0, 0, 0, 0.04);
      }

      .sidebar-header {
        padding: 1rem 1.25rem;
        min-height: 4rem;
        display: flex;
        align-items: center;
        border-bottom: 1px solid var(--p-surface-200, #e2e8f0);
        background: var(--p-surface-0, #ffffff);
      }

      .sidebar-brand-icon {
        font-size: 1.5rem;
      }

      .sidebar-nav {
        flex: 1;
        padding: 0.75rem 0.5rem;
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
      }

      .menu-group {
        display: flex;
        flex-direction: column;
        gap: 0.125rem;
      }

      .menu-group-header {
        font-size: 0.75rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 0.1em;
        color: #475569;
        padding: 0.625rem 0.875rem 0.5rem;
        margin: 0.25rem 0.5rem 0 0;
        border-left: 3px solid var(--p-primary-color, #10b981);
        background: linear-gradient(90deg, rgba(16, 185, 129, 0.08) 0%, transparent 100%);
        border-radius: 0 4px 4px 0;
      }

      .menuitem-link {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.75rem 0.875rem;
        border-radius: var(--p-border-radius-md, 8px);
        color: #1e293b;
        text-decoration: none;
        transition: background-color 0.2s, color 0.2s;
        font-size: 0.9375rem;
        font-weight: 500;
      }

      .menuitem-link i {
        flex-shrink: 0;
        width: 1.25rem;
        text-align: center;
        font-size: 1.125rem;
        color: #64748b;
        transition: color 0.2s;
      }

      .menuitem-text {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .menuitem-link:hover {
        background: var(--p-surface-100, #f1f5f9);
        color: #1e293b;
      }

      .menuitem-link:hover i {
        color: var(--p-primary-color);
      }

      .menuitem-link.active-menuitem {
        background: var(--p-primary-color);
        color: #fff;
      }

      .menuitem-link.active-menuitem i {
        color: rgba(255, 255, 255, 0.95);
      }

      .layout-sidebar-mask {
        display: none;
        position: fixed;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.4);
        z-index: 998;
        transition: opacity 0.3s;
      }

      .layout-sidebar-mask-active {
        display: block;
      }

      @media (max-width: 991px) {
        .layout-sidebar {
          transform: translateX(-100%);
        }

        .layout-sidebar.layout-sidebar-active {
          transform: translateX(0);
        }
      }

      @media (min-width: 992px) {
        .layout-sidebar-mask {
          display: none !important;
        }

        .layout-sidebar.layout-sidebar-static-inactive {
          transform: translateX(-100%);
        }
      }
    `,
  ],
})
export class SakaiSidebarComponent implements OnInit, OnDestroy {
  layoutService = inject(LayoutService);
  private auth = inject(AuthService);
  private router = inject(Router);
  private destroy$ = new Subject<void>();

  menuGroups = computed(() => {
    const perfis = [...this.auth.currentUserPerfis()].sort(
      (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
    );
    return perfis
      .map((perfil) => {
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
      })
      .filter((g) => g.rotinas.length > 0);
  });

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter((e): e is NavigationEnd => e instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe((event) => {
        this.layoutService.setActivePath(event.urlAfterRedirects);
        this.layoutService.closeMenus();
      });
    this.layoutService.setActivePath(this.router.url);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onItemClick(): void {
    if (this.layoutService.isMobile() || this.layoutService.isOverlay()) {
      this.layoutService.closeMenus();
    }
  }
}
