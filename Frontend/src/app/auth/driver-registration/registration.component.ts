import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { finalize, debounceTime, distinctUntilChanged, switchMap, map, catchError } from 'rxjs/operators';
import { of, Observable } from 'rxjs';
import { RideService } from '../../core/services/user/ride.service';
import { AuthService } from '../../core/services/auth.service';

interface Coordinates {
  lat: number;
  lng: number;
}

interface DriverRegistrationData {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  address: string;
  mobileNumber: string;
  licenceNo: string;
  password: string;
  emailVerified?: boolean;
  rating?: number;
  totalRatings?: number;
  latitude: number;
  longitude: number;
}

@Component({
  selector: 'app-driver-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class DriverRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  errorMessage = '';
  isLoading = false;
  passwordFieldType: 'password' | 'text' = 'password';
  confirmPasswordFieldType: 'password' | 'text' = 'password';
  usernameStatus: 'CHECKING' | 'AVAILABLE' | 'TAKEN' | 'INVALID' | null = null;
  usernameMessage = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private rideService: RideService
  ) {
    this.registrationForm = this.fb.group({
      username: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9_]{3,20}$/)]],
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z\s]*$/)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z\s]*$/)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required, Validators.minLength(3)]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10,15}$/)]],
      licenceNo: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(8), this.passwordStrengthValidator]],
      confirmPassword: ['', Validators.required],
      agreeToTerms: [false, Validators.requiredTrue]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.setupUsernameCheck();
  }

  private setupUsernameCheck(): void {
    const usernameControl = this.registrationForm.get('username');
    if (!usernameControl) return;

    usernameControl.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((username: string) => {
        if (!username || usernameControl.invalid) {
          this.usernameStatus = 'INVALID';
          this.usernameMessage = '3–20 chars, letters/numbers/underscore only.';
          return of(null);
        }
        this.usernameStatus = 'CHECKING';
        this.usernameMessage = '';
        return this.authService.checkUsername(username).pipe(
          map((isTaken: boolean) => {
            if (isTaken) {
              this.usernameStatus = 'TAKEN';
              usernameControl.setErrors({ usernameTaken: true });
            } else {
              this.usernameStatus = 'AVAILABLE';
              this.usernameMessage = 'Username is available!';
              // Clear any previous usernameTaken errors
              const errors = usernameControl.errors;
              if (errors && errors['usernameTaken']) {
                delete errors['usernameTaken'];
                usernameControl.setErrors(Object.keys(errors).length ? errors : null);
              }
            }
            return null;
          }),
          catchError(() => {
            this.usernameStatus = null;
            this.usernameMessage = '';
            return of(null);
          })
        );
      })
    ).subscribe();
  }

  private passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
    const v = control.value as string;
    if (!v) return null;
    const hasLowercase = /[a-z]/.test(v);
    const hasUppercase = /[A-Z]/.test(v);
    const hasNumber = /[0-9]/.test(v);
    const isLongEnough = v.length >= 8;
    
    return (hasLowercase && hasUppercase && hasNumber && isLongEnough) 
      ? null 
      : { weakPassword: true };
  }

  private passwordMatchValidator = (group: AbstractControl): ValidationErrors | null => {
    const passwordControl = group.get('password');
    const confirmPasswordControl = group.get('confirmPassword');
    
    if (!passwordControl || !confirmPasswordControl) return null;
    
    const password = passwordControl.value;
    const confirmPassword = confirmPasswordControl.value;
    
    if (password && confirmPassword && password !== confirmPassword) {
      confirmPasswordControl.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      if (confirmPasswordControl.hasError('passwordMismatch')) {
        const errors = { ...confirmPasswordControl.errors };
        delete errors['passwordMismatch'];
        confirmPasswordControl.setErrors(Object.keys(errors).length ? errors : null);
      }
      return null;
    }
  };

  togglePasswordVisibility(which: 'password' | 'confirmPassword'): void {
    if (which === 'password') {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    } else {
      this.confirmPasswordFieldType = this.confirmPasswordFieldType === 'password' ? 'text' : 'password';
    }
  }

  onSubmit(): void {
    if (this.registrationForm.invalid || this.usernameStatus === 'TAKEN' || this.usernameStatus === 'CHECKING') {
      this.registrationForm.markAllAsTouched();
      this.errorMessage = this.usernameStatus === 'TAKEN'
        ? 'Please choose a different username.'
        : 'Please correct the errors above before submitting.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formVal = this.registrationForm.value;
    const { confirmPassword, agreeToTerms, ...base } = formVal;

    // Geocode address first
    this.rideService.geocodeAddress(base.address).pipe(
      switchMap((coords: Coordinates) => {
        const driverPayload: DriverRegistrationData = {
          ...base,
          emailVerified: false,
          rating: 4.6,
          totalRatings: 112,
          latitude: coords.lat,
          longitude: coords.lng
        };
        return this.authService.registerDriver(driverPayload);
      }),
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: () => {
        this.router.navigate(['/login'], {
          queryParams: { message: 'Registration successful! Your account is pending verification.' }
        });
      },
      error: (err: any) => {
        // Your backend will return error messages like "Email is already registered."
        this.errorMessage = err?.error?.message || 'Registration failed. Please try again.';
      }
    });
  }

  // helpers for template
  isInvalid(name: string): boolean {
    const control = this.registrationForm.get(name);
    return !!(control && control.invalid && (control.touched || control.dirty));
  }

  getErrorMessage(name: string): string {
    const control = this.registrationForm.get(name);
    if (!control || !control.errors) return '';
    
    const errors = control.errors;
    
    if (errors['required']) return 'This field is required.';
    if (errors['minlength']) return `Must be at least ${errors['minlength'].requiredLength} characters.`;
    if (errors['pattern']) {
      if (name === 'mobileNumber') return 'Enter a valid phone number.';
      if (name === 'username') return 'Use letters, numbers, and underscores only.';
      if (name === 'firstName' || name === 'lastName') return 'Only letters and spaces are allowed.';
    }
    if (errors['email']) return 'Please enter a valid email address.';
    if (errors['weakPassword']) return 'Use at least 8 characters with uppercase, lowercase, and number.';
    if (errors['passwordMismatch']) return 'Passwords do not match.';
    if (errors['usernameTaken']) return 'This username is already taken.';
    
    return 'Invalid input.';
  }
}