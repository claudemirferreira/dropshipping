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

      .layout-main-container {
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
