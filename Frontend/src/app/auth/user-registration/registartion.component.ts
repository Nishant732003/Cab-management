import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { finalize, debounceTime, distinctUntilChanged, switchMap, map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-user-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class UserRegistrationComponent implements OnInit {
  registrationForm;
  errorMessage = '';
  isLoading = false;
  passwordFieldType = 'password';
  confirmPasswordFieldType = 'password';
  usernameStatus: 'CHECKING' | 'AVAILABLE' | 'TAKEN' | 'INVALID' | null = null;
  usernameMessage = '';
  passwordStrength = 0;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.registrationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z\s]*$/)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z\s]*$/)]],
      username: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9_]{3,20}$/)]],
      email: ['', [Validators.required, Validators.email]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      address: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8), this.passwordStrengthValidator.bind(this)]],
      confirmPassword: ['', Validators.required],
      agreeToTerms: [false, Validators.requiredTrue]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.setupUsernameValidation();
    this.setupPasswordStrengthCheck();
  }

  private setupUsernameValidation(): void {
    this.registrationForm.get('username')?.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(username => {
        if (!username || this.registrationForm.get('username')?.invalid) {
          this.usernameStatus = 'INVALID';
          this.usernameMessage = '3–20 chars, letters/numbers/underscore only.';
          return of(null);
        }
        this.usernameStatus = 'CHECKING';
        this.usernameMessage = '';
        return this.authService.checkUsername(username).pipe(
          map(isTaken => {
            if (isTaken) {
              this.usernameStatus = 'TAKEN';
              this.usernameMessage = 'This username is already taken.';
              this.registrationForm.get('username')?.setErrors({ usernameTaken: true });
            } else {
              this.usernameStatus = 'AVAILABLE';
              this.usernameMessage = 'Username is available!';
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

  private setupPasswordStrengthCheck(): void {
    this.registrationForm.get('password')?.valueChanges.subscribe(password => {
      this.passwordStrength = this.calculatePasswordStrength(password || '');
    });
  }

  private calculatePasswordStrength(password: string): number {
    let strength = 0;
    if (password.length >= 8) strength += 25;
    if (/[a-z]/.test(password)) strength += 25;
    if (/[A-Z]/.test(password)) strength += 25;
    if (/[0-9]/.test(password)) strength += 15;
    if (/[^a-zA-Z0-9]/.test(password)) strength += 10;
    return Math.min(strength, 100);
  }

  private passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.value;
    if (!password) return null;
    const hasLower = /[a-z]/.test(password);
    const hasUpper = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    return (password.length >= 8 && hasLower && hasUpper && hasNumber) ? null : { weakPassword: true };
  }

  passwordMatchValidator(form: any) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    if (password?.value && confirmPassword?.value && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else if (confirmPassword?.errors?.['passwordMismatch']) {
      confirmPassword.setErrors(null);
    }
    return null;
  }

  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    } else {
      this.confirmPasswordFieldType = this.confirmPasswordFieldType === 'password' ? 'text' : 'password';
    }
  }

  getPasswordStrengthText(): string {
    if (this.passwordStrength < 30) return 'Weak';
    if (this.passwordStrength < 60) return 'Fair';
    if (this.passwordStrength < 80) return 'Good';
    return 'Strong';
  }

  getPasswordStrengthColor(): string {
    if (this.passwordStrength < 30) return 'bg-red-500';
    if (this.passwordStrength < 60) return 'bg-yellow-500';
    if (this.passwordStrength < 80) return 'bg-blue-500';
    return 'bg-green-500';
  }

  onSubmit(): void {
    if (this.registrationForm.invalid || this.usernameStatus === 'TAKEN' || this.usernameStatus === 'CHECKING') {
      this.registrationForm.markAllAsTouched();
      if (this.usernameStatus === 'TAKEN') {
        this.errorMessage = 'Please choose a different username.';
      } else if (this.usernameStatus === 'CHECKING') {
        this.errorMessage = 'Please wait while we check username availability.';
      } else {
        this.errorMessage = 'Please correct the errors above before submitting.';
      }
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { confirmPassword, agreeToTerms, ...userData } = this.registrationForm.value;

    this.authService.registerUser(userData).pipe(
      finalize(() => { this.isLoading = false; })
    ).subscribe({
      next: () => {
        this.router.navigate(['/login'], {
          queryParams: { message: 'Registration successful! Please login to continue.' }
        });
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registrationForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.registrationForm.get(fieldName);
    if (!field || !field.errors || !field.touched) return '';
    const errors = field.errors;
    if (errors['required']) return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required.`;
    if (errors['email']) return 'Please enter a valid email address.';
    if (errors['minlength']) return `Must be at least ${errors['minlength'].requiredLength} characters.`;
    if (errors['pattern']) {
      if (fieldName === 'mobileNumber') return 'Please enter a valid phone number.';
      if (fieldName === 'username') return 'Username can only contain letters, numbers, and underscores.';
      if (fieldName === 'firstName' || fieldName === 'lastName') return 'Name can only contain letters and spaces.';
    }
    if (errors['weakPassword']) return 'Password must contain uppercase, lowercase, and a number.';
    if (errors['passwordMismatch']) return 'Passwords do not match.';
    if (errors['usernameTaken']) return 'This username is already taken.';
    return 'Invalid input.';
  }
}
