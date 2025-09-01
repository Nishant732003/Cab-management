// user-registration.component.ts - Final Version with API Call
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-user-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class UserRegistrationComponent {
  registrationForm;
  errorMessage = '';
  isLoading = false;
  passwordFieldType = 'password';
  confirmPasswordFieldType = 'password';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.registrationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      agreeToTerms: [false, Validators.requiredTrue]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: any) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else {
      confirmPassword.setErrors(null);
    }
    return null;
  }

  togglePasswordVisibility(field: string) {
    if (field === 'password') {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    } else {
      this.confirmPasswordFieldType = this.confirmPasswordFieldType === 'password' ? 'text' : 'password';
    }
  }

  onSubmit() {
    if (this.registrationForm.invalid) {
        this.registrationForm.markAllAsTouched(); // Show validation errors on all fields
        return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Prepare the data to send to the API
    const { confirmPassword, agreeToTerms, ...userData } = this.registrationForm.value;

    // Call the AuthService to register the user
    this.authService.registerUser(userData).pipe(
      finalize(() => {
        this.isLoading = false; // Stop the loading spinner
      })
    ).subscribe({
      next: (response) => {
        // On success, navigate to the login page with a success message
        this.router.navigate(['/login'], {
          queryParams: { message: 'Registration successful! Please login to continue.' }
        });
      },
      error: (error) => {
        // On failure, display the error message from the backend
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}