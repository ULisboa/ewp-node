import { Route } from '@angular/router';
import { AppLayoutComponent } from './layout/app.layout.component';
import { adminAuthCanMatchGuard } from './shared/guards/admin/admin-auth.guard';

export const appRoutes: Route[] = [
    {
        path: 'admin',
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: '/admin/communications/logs',
            }, 
            {
                path: 'communications/logs',
                pathMatch: 'full',
                component: AppLayoutComponent,
                loadChildren: () => import("./modules/admin/dashboard/admin-dashboard.module").then(m => m.AdminDashboardModule),
                canMatch: [adminAuthCanMatchGuard]
            }, 
            {
                path: 'auth',
                pathMatch: 'full',
                loadChildren: () => import("./modules/admin/auth/admin-auth.module").then(m => m.AdminAuthModule)
            },
            {
                path: '**',
                redirectTo: '/admin/auth'
            }
        ]
    },
    {
        path: 'error',
        loadChildren: () => import("./modules/error/error.module").then(m => m.ErrorModule)
    },
    {
        path: '**',
        redirectTo: '/error/not-found'
    }
];
