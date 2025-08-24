import { Component } from '@angular/core';

@Component({
  selector: 'app-driver',
   standalone: false,

  templateUrl: './driver.component.html',
})
export class AdminComponent {
  showAdminAlert = true;
  systemVersion = '2.4.1';
}