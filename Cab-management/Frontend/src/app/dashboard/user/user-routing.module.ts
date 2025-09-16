import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersCustomerComponent } from './components/user-dashboard/user.component';

// Import page components
import { BookRideComponent } from './pages/book-ride/bookRide.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { TripHistoryComponent } from './pages/triphistory/trips.component';

// Import dashboard components


const routes: Routes = [
  {
    path: '',
    component: UsersCustomerComponent,
    children: [
      {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full'
      },
      {
        path: 'overview',
        component: OverviewComponent
      },
      {
        path: 'book-ride',
        component: BookRideComponent
      },

      {
        path: 'trip-history',
        component: TripHistoryComponent
      },
     
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }