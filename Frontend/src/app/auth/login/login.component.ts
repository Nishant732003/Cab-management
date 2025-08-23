import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    setTimeout(() => {
      const formValue = this.loginForm.value;
      
      // Safely extract email and password with null checks
      const email = formValue.email?.trim() || '';
      const password = formValue.password?.trim() || '';
      
      // Additional validation
      if (!email || !password) {
        this.errorMessage = 'Please enter both email and password';
        this.isLoading = false;
        return;
      }
      
      const loginSuccess = this.authService.login(email, password);

      if (loginSuccess) {
        const currentUser = this.authService.getCurrentUser();
        
        // Navigate based on user role
        switch(currentUser?.role) {
          case 'admin':
            this.router.navigate(['/admin']);
            break;
          case 'driver':
            this.router.navigate(['/driver']); // Enable this when driver module is ready
            break;
          case 'user':
            this.router.navigate(['/user']); // Add user dashboard route
            break;
          default:
            this.router.navigate(['/login']);
        }
      } else {
        this.errorMessage = 'Invalid email or password';
      }

      this.isLoading = false;
    }, 800);
  }
}