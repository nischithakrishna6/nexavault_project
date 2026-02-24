import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PasswordResetService } from '../../services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  step: 'email' | 'otp' | 'password' = 'email';

  email: string = '';
  otp: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  showPassword: boolean = false;
  otpSentTime: Date | null = null;

  constructor(
    private passwordResetService: PasswordResetService,
    private router: Router
  ) {}

  sendOTP(): void {
    if (!this.email) {
      this.errorMessage = 'Please enter your email';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.passwordResetService.sendOTP(this.email).subscribe({
      next: (response) => {
        if (response.success) {
          this.successMessage = 'OTP sent to your email! Check console for OTP (dev mode)';
          this.step = 'otp';
          this.otpSentTime = new Date();
        } else {
          this.errorMessage = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to send OTP';
        this.isLoading = false;
      }
    });
  }

  verifyOTP(): void {
    if (!this.otp || this.otp.length !== 6) {
      this.errorMessage = 'Please enter a valid 6-digit OTP';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.passwordResetService.verifyOTP(this.email, this.otp).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.step = 'password';
          this.successMessage = 'OTP verified! Create your new password';
        } else {
          this.errorMessage = 'Invalid or expired OTP';
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'OTP verification failed';
        this.isLoading = false;
      }
    });
  }

  resetPassword(): void {
    this.errorMessage = '';

    if (!this.newPassword || this.newPassword.length < 12) {
      this.errorMessage = 'Password must be at least 12 characters';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    this.isLoading = true;

    this.passwordResetService.resetPassword(this.email, this.otp, this.newPassword).subscribe({
      next: (response) => {
        if (response.success) {
          this.successMessage = 'Password reset successfully! Redirecting to login...';
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        } else {
          this.errorMessage = response.message;
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to reset password';
        this.isLoading = false;
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  resendOTP(): void {
    this.otp = '';
    this.sendOTP();
  }
}
