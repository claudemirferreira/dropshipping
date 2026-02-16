import { Component } from '@angular/core';

@Component({
  selector: 'app-sakai-footer',
  standalone: true,
  template: `
    <footer class="layout-footer">
      <span class="footer-copyright">© {{ currentYear }} 7bit</span>
      <span class="footer-separator">•</span>
      <a href="mailto:claudemirramosferreira&#64;gmail.com" class="footer-email">claudemirramosferreira&#64;gmail.com</a>
    </footer>
  `,
  styles: [
    `
      .layout-footer {
        padding: 0.875rem 1.5rem;
        background: var(--app-surface-sidebar);
        border-top: 1px solid var(--app-border-color);
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.8125rem;
        color: var(--app-text-muted);
      }

      .footer-separator {
        opacity: 0.5;
      }

      .footer-email {
        color: var(--app-text-muted);
        text-decoration: none;
        transition: color 0.2s;
      }

      .footer-email:hover {
        color: var(--app-primary);
      }
    `,
  ],
})
export class SakaiFooterComponent {
  currentYear = new Date().getFullYear();
}
