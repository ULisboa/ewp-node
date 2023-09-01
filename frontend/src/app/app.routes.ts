import { Route } from '@angular/router';
import { AppLayoutComponent } from './layout/app.layout.component';
import { adminAuthCanMatchGuard } from '@ewp-node-frontend/admin/auth/data-access';
import { NotFoundComponent } from '@ewp-node-frontend/not-found';

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
                loadChildren: () => import("@ewp-node-frontend/admin/dashboard/communications/logs").then(m => m.AdminDashboardCommunicationsLogsModule),
                canMatch: [adminAuthCanMatchGuard]
            }, 
            {
                path: 'auth',
                pathMatch: 'full',
                loadChildren: () => import("@ewp-node-frontend/admin/auth/feature-login").then(m => m.AdminAuthFeatureLoginModule)
            },
            {
                path: '**',
                redirectTo: '/admin/auth'
            }
        ]
    },
    {
        path: '**',
        component: NotFoundComponent
    }
];
