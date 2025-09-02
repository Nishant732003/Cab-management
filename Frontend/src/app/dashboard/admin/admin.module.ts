import { NgModule } from '@angular/core';
// FIX: Import CommonModule and FormsModule
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { AdminRoutingModule } from './admin-routing.module';

import { AdminComponent } from './components/admin-dashbaord/admin.component';
import { NavbarComponent } from './components/admin-navbar/navbar.component';
import { SidebarComponent } from './components/admin-sidebar/sidebar.component';
// Note: We don't import or declare UserComponent here because it's standalone

@NgModule({
  declarations: [
    AdminComponent,
    NavbarComponent,
    SidebarComponent,
  ],
  imports: [
    // FIX: Add these two modules
    CommonModule, 
    FormsModule,  
    AdminRoutingModule
  ]
})
export class AdminModule { }