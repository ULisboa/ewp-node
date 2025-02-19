import { Route } from '@angular/router';
import { adminAuthCanMatchGuard } from './shared/guards/admin/admin-auth.guard';
import { AppLayout } from './layout/component/app.layout';

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
                component: AppLayout,
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
