import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './components/admin-dashbaord/admin.component';
import { NavbarComponent } from './components/admin-navbar/navbar.component';
import { SidebarComponent } from './components/admin-sidebar/sidebar.component';

// DO NOT IMPORT STANDALONE COMPONENTS HERE (Overview, User, Driver, etc.)

@NgModule({
  declarations: [
    AdminComponent, // This is part of the module
    NavbarComponent, // This is part of the module
    SidebarComponent // This is part of the module
    // REMOVE ALL STANDALONE COMPONENTS FROM HERE
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule
  ]
})
export class AdminModule { }