// driver-registration.component.ts - Final Version with API Call
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-driver-registration',
  standalone: true, // Set to true as it's not in a module
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class DriverRegistrationComponent {
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
      licenseNumber: ['', [Validators.required, Validators.minLength(6)]],
      vehicleType: ['', Validators.required],
      vehicleModel: ['', [Validators.required, Validators.minLength(2)]],
      vehicleYear: ['', [Validators.required, Validators.min(1990), Validators.max(new Date().getFullYear() + 1)]],
      licensePlate: ['', [Validators.required, Validators.minLength(4)]],
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
        this.registrationForm.markAllAsTouched();
        return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { confirmPassword, agreeToTerms, ...driverData } = this.registrationForm.value;

    // Call the AuthService to register the driver
    this.authService.registerDriver(driverData).pipe(
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (response) => {
        // On success, navigate to login with a message about verification
        this.router.navigate(['/login'], {
          queryParams: { message: 'Registration successful! Your account is pending admin verification.' }
        });
      },
      error: (error) => {
        // On failure, display the error message
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}