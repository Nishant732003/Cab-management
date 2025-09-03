// admin-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './components/admin-dashbaord/admin.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { UserComponent } from './pages/user/user.component';
// import { UsersManagementComponent } from './pages/users-management/users-management.component';
import { DriverComponent } from './pages/driver/driver.component';
import { AdminVerificationComponent } from './pages/admin-verification/admin-verification.component'; // Import new component
import { TripsComponent } from './pages/trips/trips.component'; // Import new component

const routes: Routes = [
  {
    path: '', 
    component: AdminComponent,
    children: [
      // --- Default route redirects to the overview page ---
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      // --- Route for the main overview/dashboard page ---
      { path: 'overview', component: OverviewComponent },
      // --- Route for managing users ---
      { path: 'users', component: UserComponent },
      // --- Route for managing drivers ---
      { path: 'drivers', component: DriverComponent },
      // --- CORRECTED ROUTE for trip management ---
      { path: 'trip-management', component: TripsComponent },
      // --- CORRECTED ROUTE for admin verification ---
      { path: 'admin-verification', component: AdminVerificationComponent },
      // --- CRITICAL FIX: The wildcard route MUST be last ---
      { path: '**', redirectTo: 'overview' } ,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)], // ← forChild() for feature modules
  exports: [RouterModule]
})
export class AdminRoutingModule { } // ← Fixed spelling: 'Rooting' → 'Routing'