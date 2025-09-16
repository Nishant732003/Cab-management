import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DriverComponent } from './components/driver-dashboard/driver.component';
import { TripsComponent } from './pages/trips/trip.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { VehicleComponent } from './pages/vehicle/vehicle.component';
const routes: Routes = [
  {
    path: '',
    component: DriverComponent,
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
        path: 'trips',
        component: TripsComponent
      },
      {path:'vehicle',
        component:VehicleComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DriverRoutingModule { }