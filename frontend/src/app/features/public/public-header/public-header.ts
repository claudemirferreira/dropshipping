import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-public-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="public-header-bar">
      <div class="header-content">
        <a routerLink="/" class="header-logo">
          <i class="pi pi-box"></i>
          <span>Dropshipping</span>
        </a>
        <nav class="header-nav">
          <a routerLink="/planos">Planos</a>
          <a routerLink="/contato">Contato</a>
          <a routerLink="/login">Entrar</a>
        </nav>
      </div>
    </header>
  `,
  styles: [
    `
      .public-header-bar {
        background: var(--app-primary, #0d9488);
        color: #fff;
        padding: 1rem 1.5rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      }

      .header-content {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .header-logo {
        display: flex;
        align-items: center;
        gap: 0.625rem;
        text-decoration: none;
        color: #fff;
        font-weight: 700;
        font-size: 1.25rem;
      }

      .header-logo i {
        font-size: 1.5rem;
      }

      .header-nav {
        display: flex;
        gap: 1.5rem;
        align-items: center;
      }

      .header-nav a {
        color: rgba(255, 255, 255, 0.9);
        text-decoration: none;
        font-size: 0.9375rem;
        font-weight: 500;
        padding: 0.5rem 0.75rem;
        border-radius: 6px;
        transition: background-color 0.2s;
      }

      .header-nav a:hover {
        background: rgba(255, 255, 255, 0.12);
      }
    `,
  ],
})
export class PublicHeaderComponent {}
