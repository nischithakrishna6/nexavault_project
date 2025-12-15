package com.fintech.platform.service;

import com.fintech.platform.dto.ForgotPasswordRequest;
import com.fintech.platform.dto.ResetPasswordRequest;
import com.fintech.platform.dto.VerifyOTPRequest;
import com.fintech.platform.model.OTP;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.OTPRepository;
import com.fintech.platform.repository.UserRepository;
import com.fintech.platform.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void sendOTP(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(request.getEmail());

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // Save OTP (expires in 10 minutes)
        OTP otp = new OTP();
        otp.setEmail(request.getEmail());
        otp.setOtp(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setIsUsed(false);

        otpRepository.save(otp);

        // In production, send email here
        // For now, we'll just log it
        System.out.println("OTP for " + request.getEmail() + ": " + otpCode);
        System.out.println("OTP expires at: " + otp.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public boolean verifyOTP(VerifyOTPRequest request) {
        return otpRepository.findByEmailAndOtpAndIsUsedFalseAndExpiresAtAfter(
                request.getEmail(),
                request.getOtp(),
                LocalDateTime.now()
        ).isPresent();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Verify OTP
        OTP otp = otpRepository.findByEmailAndOtpAndIsUsedFalseAndExpiresAtAfter(
                request.getEmail(),
                request.getOtp(),
                LocalDateTime.now()
        ).orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate new password
        PasswordValidator.ValidationResult validation = PasswordValidator.validate(
                request.getNewPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        if (!validation.isValid()) {
            throw new RuntimeException(validation.getError());
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark OTP as used
        otp.setIsUsed(true);
        otpRepository.save(otp);
    }
}