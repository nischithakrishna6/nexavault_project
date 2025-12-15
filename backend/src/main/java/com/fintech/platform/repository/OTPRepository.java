package com.fintech.platform.repository;

import com.fintech.platform.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByEmailAndOtpAndIsUsedFalseAndExpiresAtAfter(
            String email, String otp, LocalDateTime now);

    void deleteByEmail(String email);
}