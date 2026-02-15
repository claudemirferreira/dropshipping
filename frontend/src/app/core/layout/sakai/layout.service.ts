import { Injectable, signal, computed } from '@angular/core';

export interface LayoutConfig {
  preset: string;
  primary: string;
  surface: string | undefined | null;
  darkTheme: boolean;
  menuMode: 'static' | 'overlay';
}

export interface LayoutState {
  staticMenuDesktopInactive: boolean;
  overlayMenuActive: boolean;
  configSidebarVisible: boolean;
  mobileMenuActive: boolean;
  menuHoverActive: boolean;
  activePath: string | null;
}

@Injectable({ providedIn: 'root' })
export class LayoutService {
  layoutConfig = signal<LayoutConfig>({
    preset: 'Aura',
    primary: 'emerald',
    surface: null,
    darkTheme: false,
    menuMode: 'static',
  });

  layoutState = signal<LayoutState>({
    staticMenuDesktopInactive: false,
    overlayMenuActive: false,
    configSidebarVisible: false,
    mobileMenuActive: false,
    menuHoverActive: false,
    activePath: null as string | null,
  });

  theme = computed(() => (this.layoutConfig().darkTheme ? 'light' : 'dark'));
  isSidebarActive = computed(
    () =>
      this.layoutState().overlayMenuActive || this.layoutState().mobileMenuActive
  );
  isDarkTheme = computed(() => this.layoutConfig().darkTheme);
  getPrimary = computed(() => this.layoutConfig().primary);
  getSurface = computed(() => this.layoutConfig().surface);
  isOverlay = computed(() => this.layoutConfig().menuMode === 'overlay');

  setActivePath(path: string | null): void {
    this.layoutState.update((s) => ({ ...s, activePath: path }));
  }

  closeMenus(): void {
    this.layoutState.update((s) => ({
      ...s,
      overlayMenuActive: false,
      mobileMenuActive: false,
      menuHoverActive: false,
    }));
  }

  toggleDarkMode(): void {
    this.layoutConfig.update((c) => ({ ...c, darkTheme: !c.darkTheme }));
    this.applyDarkMode();
  }

  private applyDarkMode(): void {
    const dark = this.layoutConfig().darkTheme;
    if (dark) {
      document.documentElement.classList.add('app-dark');
    } else {
      document.documentElement.classList.remove('app-dark');
    }
  }

  onMenuToggle(): void {
    const state = this.layoutState();
    if (this.isOverlay()) {
      this.layoutState.update((s) => ({
        ...s,
        overlayMenuActive: !state.overlayMenuActive,
      }));
    } else if (this.isDesktop()) {
      this.layoutState.update((s) => ({
        ...s,
        staticMenuDesktopInactive: !s.staticMenuDesktopInactive,
      }));
    } else {
      this.layoutState.update((s) => ({
        ...s,
        mobileMenuActive: !s.mobileMenuActive,
      }));
    }
  }

  isDesktop(): boolean {
    return typeof window !== 'undefined' && window.innerWidth > 991;
  }

  isMobile(): boolean {
    return !this.isDesktop();
  }
}
