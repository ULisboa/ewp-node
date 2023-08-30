import { Route } from '@angular/router';
import { AppLayoutComponent } from './layout/app.layout.component';
import { NxWelcomeComponent } from './nx-welcome.component';
import { adminAuthCanMatchGuard } from '@ewp-node-frontend/admin/auth/data-access';

export const appRoutes: Route[] = [
    {
        path: '', component: AppLayoutComponent,
        children: [
            { path: '', component: NxWelcomeComponent }
        ]
    },
    {
        path: 'admin',
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: '/admin/communications/logs',
                canMatch: [adminAuthCanMatchGuard]
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
    }
];
