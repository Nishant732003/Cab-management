
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './components/admin-dashbaord/admin.component';
import { NavbarComponent } from './components/admin-navbar/navbar.component';
import { SidebarComponent } from './components/admin-sidebar/sidebar.component';
import { OverviewComponent } from './pages/overview/overview.component';
import { DriverComponent } from './pages/driver/driver.component';
import { UsersComponent } from './pages/user/user.component';

@NgModule({
  declarations: [
    AdminComponent,
    NavbarComponent,
    SidebarComponent,
    OverviewComponent,
    DriverComponent,
    UsersComponent
  ],
  imports: [
    CommonModule,      
    FormsModule,      
    RouterModule,      
    AdminRoutingModule
  ]
})
export class AdminModule { }