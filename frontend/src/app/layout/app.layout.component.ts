import { Component, OnDestroy, Renderer2, ViewChild } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, Observable, Subscription } from 'rxjs';
import { LayoutService } from "./service/app.layout.service";
import { AppTopBarComponent } from './app.topbar.component';
import { CommonModule, NgClass } from '@angular/common';
import { AppFooterComponent } from './app.footer.component';
import { AdminAuthState, selectAdminAuthState } from '@ewp-node-frontend/admin/auth/data-access';
import { Store } from '@ngrx/store';

@Component({
    selector: 'app-layout',
    templateUrl: './app.layout.component.html',
    standalone: true,
    imports: [NgClass, AppTopBarComponent, RouterOutlet, AppFooterComponent, CommonModule]
})
export class AppLayoutComponent {

    adminAuthState$: Observable<AdminAuthState>;

    menuOutsideClickListener: any;

    profileMenuOutsideClickListener: any;

    @ViewChild(AppTopBarComponent) appTopbar!: AppTopBarComponent;

    constructor(public layoutService: LayoutService, public renderer: Renderer2, public router: Router, store: Store) {
        this.router.events.pipe(filter(event => event instanceof NavigationEnd))
            .subscribe(() => {
                this.hideProfileMenu();
            });

        this.adminAuthState$ = store.select(selectAdminAuthState);
    }

    hideProfileMenu() {
        this.layoutService.state.profileSidebarVisible = false;
        if (this.profileMenuOutsideClickListener) {
            this.profileMenuOutsideClickListener();
            this.profileMenuOutsideClickListener = null;
        }
    }

    blockBodyScroll(): void {
        if (document.body.classList) {
            document.body.classList.add('blocked-scroll');
        }
        else {
            document.body.className += ' blocked-scroll';
        }
    }

    unblockBodyScroll(): void {
        if (document.body.classList) {
            document.body.classList.remove('blocked-scroll');
        }
        else {
            document.body.className = document.body.className.replace(new RegExp('(^|\\b)' +
                'blocked-scroll'.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
        }
    }

    get containerClass() {
        return {
            'layout-theme-light': this.layoutService.config.colorScheme === 'light',
            'layout-theme-dark': this.layoutService.config.colorScheme === 'dark',
            'p-input-filled': this.layoutService.config.inputStyle === 'filled',
            'p-ripple-disabled': !this.layoutService.config.ripple
        }
    }
}
