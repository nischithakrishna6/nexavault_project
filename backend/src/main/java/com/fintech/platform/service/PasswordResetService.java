package com.fintech.platform.service;

import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;  // ✅ ADD THIS

    private final Map<String, OTPData> otpStore = new HashMap<>();

    @Transactional
    public ApiResponse<String> sendOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String otp = generateOTP();
        otpStore.put(email, new OTPData(otp, LocalDateTime.now().plusMinutes(10)));

        // ✅ REPLACE console print with actual email
        try {
            emailService.sendOTPEmail(email, otp);
            System.out.println(" OTP sent to email: " + email);
        } catch (Exception e) {
            System.err.println(" Failed to send email: " + e.getMessage());
            // Fallback: still log to console in dev mode
            System.out.println(" [DEV MODE] OTP for " + email + ": " + otp);
        }

        return new ApiResponse<>(true, "OTP sent to your email", null);
    }

    public ApiResponse<Boolean> verifyOTP(String email, String otp) {
        OTPData otpData = otpStore.get(email);

        if (otpData == null) {
            return new ApiResponse<>(false, "OTP not found or expired", false);
        }

        if (LocalDateTime.now().isAfter(otpData.expiryTime)) {
            otpStore.remove(email);
            return new ApiResponse<>(false, "OTP has expired", false);
        }

        if (!otpData.otp.equals(otp)) {
            return new ApiResponse<>(false, "Invalid OTP", false);
        }

        return new ApiResponse<>(true, "OTP verified successfully", true);
    }

    @Transactional
    public ApiResponse<String> resetPassword(String email, String otp, String newPassword) {
        ApiResponse<Boolean> verifyResponse = verifyOTP(email, otp);
        if (!verifyResponse.isSuccess()) {
            return new ApiResponse<>(false, verifyResponse.getMessage(), null);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStore.remove(email);

        return new ApiResponse<>(true, "Password reset successfully", null);
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private static class OTPData {
        String otp;
        LocalDateTime expiryTime;

        OTPData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}