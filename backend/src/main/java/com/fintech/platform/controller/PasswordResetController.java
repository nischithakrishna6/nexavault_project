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
@CrossOrigin(origins = "http://localhost:4200")  // ✅ Add this for frontend
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOTP(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            // ✅ Extract email from request object
            ApiResponse<String> response = passwordResetService.sendOTP(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        try {
            // ✅ Extract email and otp from request object
            ApiResponse<Boolean> response = passwordResetService.verifyOTP(
                    request.getEmail(),
                    request.getOtp()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), false));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            // ✅ Extract all three fields from request object
            ApiResponse<String> response = passwordResetService.resetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}