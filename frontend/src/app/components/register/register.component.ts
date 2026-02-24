import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';

interface PasswordValidation {
  minLength: boolean;
  hasUpperCase: boolean;
  hasLowerCase: boolean;
  hasNumber: boolean;
  hasSpecialChar: boolean;
  complexityCount: number;
  noPersonalInfo: boolean;
  noRepetition: boolean;
  notCommon: boolean;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: ''
  };

  confirmPassword: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;
  showPassword: boolean = false;

  passwordValidation: PasswordValidation = {
    minLength: false,
    hasUpperCase: false,
    hasLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false,
    complexityCount: 0,
    noPersonalInfo: true,
    noRepetition: true,
    notCommon: true
  };

  showPasswordRequirements: boolean = false;

  commonPasswords = [
    'password', '123456', '123456789', '12345678', '12345', 'qwerty',
    'password123', 'admin', 'welcome', 'monkey', 'letmein', 'abc123'
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

togglePasswordVisibility(): void {
  this.showPassword = !this.showPassword;
}
  onPasswordChange(): void {
    const password = this.registerData.password;
    this.showPasswordRequirements = password.length > 0;

    // Check basic requirements
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSpecialChar = /[@$!%*?&#^()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

    // Count complexity
    let complexityCount = 0;
    if (hasUpperCase) complexityCount++;
    if (hasLowerCase) complexityCount++;
    if (hasNumber) complexityCount++;
    if (hasSpecialChar) complexityCount++;

    // Check for personal info
    const lowerPassword = password.toLowerCase();
    const noPersonalInfo = !lowerPassword.includes(this.registerData.firstName.toLowerCase()) &&
                          !lowerPassword.includes(this.registerData.lastName.toLowerCase()) &&
                          !lowerPassword.includes(this.registerData.email.split('@')[0].toLowerCase());

    // Check for repetition (aaaa, 1111)
    const noRepetition = !/(.)\1{5,}/.test(password);

    // Check if common password
    const notCommon = !this.commonPasswords.some(common =>
      lowerPassword.includes(common)
    );

    this.passwordValidation = {
      minLength: password.length >= 12,
      hasUpperCase,
      hasLowerCase,
      hasNumber,
      hasSpecialChar,
      complexityCount,
      noPersonalInfo,
      noRepetition,
      notCommon
    };
  }

  isPasswordValid(): boolean {
    return this.passwordValidation.minLength &&
           this.passwordValidation.complexityCount >= 3 &&
           this.passwordValidation.noPersonalInfo &&
           this.passwordValidation.noRepetition &&
           this.passwordValidation.notCommon;
  }

  getPasswordStrength(): string {
    if (!this.registerData.password) return '';
    if (!this.isPasswordValid()) return 'weak';
    if (this.passwordValidation.complexityCount === 4) return 'strong';
    return 'medium';
  }

  onSubmit(): void {
    this.errorMessage = '';

    // Validation
    if (!this.registerData.email || !this.registerData.password ||
        !this.registerData.firstName || !this.registerData.lastName) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    if (!this.isPasswordValid()) {
      this.errorMessage = 'Password does not meet security requirements';
      return;
    }

    this.isLoading = true;

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        if (response.success) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        this.isLoading = false;
      }
    });
  }
}
