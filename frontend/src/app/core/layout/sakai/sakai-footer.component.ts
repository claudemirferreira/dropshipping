import { Component } from '@angular/core';

@Component({
  selector: 'app-sakai-footer',
  standalone: true,
  template: `
    <footer class="layout-footer">
      <span>Dropshipping</span>
      <span class="footer-separator">•</span>
      <span>Layout inspirado em Sakai (PrimeNG)</span>
    </footer>
  `,
  styles: [
    `
      .layout-footer {
        padding: 0.875rem 1.5rem;
        background: var(--p-surface-50, #f8fafc);
        border-top: 1px solid var(--p-surface-200, #e2e8f0);
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.8125rem;
        color: var(--p-text-muted-color, #64748b);
      }

      .footer-separator {
        opacity: 0.5;
      }
    `,
  ],
})
export class SakaiFooterComponent {}
