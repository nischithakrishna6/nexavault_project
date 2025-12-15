package com.fintech.platform.controller;

import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.dto.ForgotPasswordRequest;
import com.fintech.platform.dto.ResetPasswordRequest;
import com.fintech.platform.dto.VerifyOTPRequest;
import com.fintech.platform.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOTP(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.sendOTP(request);
            return ResponseEntity.ok(ApiResponse.success("OTP sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        try {
            boolean isValid = passwordResetService.verifyOTP(request);
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", true));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}