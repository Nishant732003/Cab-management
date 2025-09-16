import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
  ],
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
    // This defines the form controls. The names here MUST match the
    // formControlName attributes in the HTML.
    this.loginForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    };

    this.isLoading = true;
    this.errorMessage = '';

    const formValue = this.loginForm.value;
    const identifier = formValue.identifier?.trim() || '';
    const password = formValue.password?.trim() || '';

    if (!identifier || !password) {
      this.errorMessage = 'Please enter both username/email and password';
      this.isLoading = false;
      return;
    }

    this.authService.login(identifier, password)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe({
        next: (response) => {
          if (response.userId && response.token) {
            const currentUser = this.authService.getCurrentUser();
            
            // Redirect user based on their role after successful login
            switch (currentUser?.userType) {
              case 'Admin':
                this.router.navigate(['/admin']);
                break;
              case 'Driver':
                this.router.navigate(['/driver']);
                break;
              case 'Customer':
              case 'User':
                this.router.navigate(['/user']);
                break;
              default:
                this.router.navigate(['/login']);
            }
          } else {
            this.errorMessage = response.message || 'Login failed';
          }
        },
        error: (error) => {
          console.error('Login error:', error);
          if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else if (error.message) {
            this.errorMessage = error.message;
          } else {
            this.errorMessage = 'Login failed. Please try again.';
          }
        }
      });
  }
}
