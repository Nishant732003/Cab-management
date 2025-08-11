import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm; // Declare without initialization
  errorMessage = '';
  isLoading = false;

  private users = [
    { email: 'admin@cab.com', password: 'admin123', role: 'admin' },
    { email: 'driver@cab.com', password: 'driver123', role: 'driver' },
    { email: 'user@cab.com', password: 'user123', role: 'customer' }
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // Initialize loginForm in constructor after fb is available
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    setTimeout(() => {
      const formValue = this.loginForm.value;
      const user = this.users.find(
        u => u.email === formValue.email && u.password === formValue.password
      );

      if (user) {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('currentUser', JSON.stringify({
            email: user.email,
            role: user.role,
            token: 'mock-token-' + Math.random().toString(36).substring(2)
          }));
        }
        this.router.navigate(['/dashboard']);
      } else {
        this.errorMessage = 'Invalid email or password';
      }
      
      this.isLoading = false;
    }, 800);
  }
}