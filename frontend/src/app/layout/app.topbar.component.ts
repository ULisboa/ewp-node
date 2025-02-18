import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { LayoutService } from "./service/app.layout.service";
import { RouterLink } from '@angular/router';
import { CommonModule, NgClass, NgIf } from '@angular/common';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { MenubarModule } from 'primeng/menubar';
import { adminAuthActions } from '../shared/states/admin-auth/admin-auth.actions';
import { AdminAuthState } from '../shared/states/admin-auth/admin-auth.reducer';

@Component({
    selector: 'app-topbar',
    templateUrl: './app.topbar.component.html',
    standalone: true,
    imports: [RouterLink, NgClass, NgIf, MenubarModule, CommonModule]
})
export class AppTopBarComponent implements OnInit {

    @Input()
    adminAuthState$?: Observable<AdminAuthState>;

    menuItems!: MenuItem[];

    @ViewChild('menubutton') menuButton!: ElementRef;

    @ViewChild('topbarmenubutton') topbarMenuButton!: ElementRef;

    @ViewChild('topbarmenu') menu!: ElementRef;

    constructor(public layoutService: LayoutService, private store: Store) { }

    ngOnInit() {
        this.menuItems = [
            {
                label: 'Communication Logs',
                icon: 'pi pi-fw pi-history',
                routerLink: '/admin'
            }
        ];
    }

    logout() {
        this.store.dispatch(adminAuthActions.logout());
    }
}
