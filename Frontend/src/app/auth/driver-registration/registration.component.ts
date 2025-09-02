import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { finalize, debounceTime, distinctUntilChanged, switchMap, map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-driver-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class DriverRegistrationComponent implements OnInit {
  registrationForm;
  errorMessage = '';
  isLoading = false;
  passwordFieldType = 'password';
  confirmPasswordFieldType = 'password';
  usernameStatus: 'CHECKING' | 'AVAILABLE' | 'TAKEN' | 'INVALID' | null = null;
  usernameMessage = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.registrationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      username: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9_]{3,20}$/)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10,15}$/)]],
      licenseNumber: ['', [Validators.required, Validators.minLength(6)]],
      cab: this.fb.group({
          cabType: ['', Validators.required],
          carModel: ['', [Validators.required, Validators.minLength(2)]],
          carNumber: ['', [Validators.required, Validators.minLength(4)]],
      }),
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      agreeToTerms: [false, Validators.requiredTrue]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
      this.registrationForm.get('username')?.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(username => {
            if (!username || (this.registrationForm.get('username')?.invalid)) {
                this.usernameStatus = 'INVALID';
                this.usernameMessage = 'Username must be 3-20 characters long and contain only letters, numbers, or underscores.';
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
    if (this.registrationForm.invalid || this.usernameStatus === 'TAKEN' || this.usernameStatus === 'CHECKING') {
      this.registrationForm.markAllAsTouched();
        if (this.usernameStatus === 'TAKEN') {
            this.errorMessage = 'Please choose a different username.';
        }
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const { confirmPassword, agreeToTerms, ...driverData } = this.registrationForm.value;
    
    this.authService.registerDriver(driverData).pipe(
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (response) => {
        this.router.navigate(['/login'], {
          queryParams: { message: 'Registration successful! Your account is pending admin verification.' }
        });
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
