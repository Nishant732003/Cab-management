// admin-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './components/admin-dashbaord/admin.component';
import { OverviewComponent } from './pages/overview/overview.component';
// import { UsersManagementComponent } from './pages/users-management/users-management.component';
import { DriverComponent } from './pages/driver/driver.component';

const routes: Routes = [
  {
    path: '', // ← Matches '/admin'
    component: AdminComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: OverviewComponent },
    //   { path: 'users', component: UsersManagementComponent },
      { path: 'drivers', component: DriverComponent },
      { path: '**', redirectTo: 'overview' } // ← Catch invalid routes
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)], // ← forChild() for feature modules
  exports: [RouterModule]
})
export class AdminRoutingModule { } // ← Fixed spelling: 'Rooting' → 'Routing'