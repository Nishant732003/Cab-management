// app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  
  { 
    path: 'admin',
    loadChildren: () => import('./dashboard/admin/admin.module').then(m => m.AdminModule),
    canActivate: [AuthGuard, AdminGuard]
  },
  
  // Uncomment when driver module is ready
  // { 
  //   path: 'driver',
  //   loadChildren: () => import('./dashboard/driver/driver.module').then(m => m.DriverModule),
  //   canActivate: [AuthGuard]
  // },
  
  // Add user dashboard route when ready
  // { 
  //   path: 'user',
  //   loadChildren: () => import('./dashboard/user/user.module').then(m => m.UserModule),
  //   canActivate: [AuthGuard]
  // },
  
  // Default and wildcard routes
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];