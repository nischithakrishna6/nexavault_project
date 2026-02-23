package com.fintech.platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    public void sendOTPEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP - " + appName);

            String htmlContent = buildOTPEmailTemplate(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ OTP email sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private String buildOTPEmailTemplate(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background: #f9f9f9;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .header h1 {
                        color: #7c5cbf;
                        margin: 0;
                    }
                    .otp-box {
                        background: white;
                        border: 2px dashed #7c5cbf;
                        border-radius: 8px;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                    }
                    .otp-code {
                        font-size: 32px;
                        font-weight: bold;
                        color: #7c5cbf;
                        letter-spacing: 8px;
                        margin: 10px 0;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        color: #666;
                        font-size: 14px;
                    }
                    .warning {
                        background: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    
                    <p>Hello,</p>
                    
                    <p>You requested to reset your password for <strong>%s</strong>.</p>
                    
                    <p>Use the following One-Time Password (OTP) to complete the process:</p>
                    
                    <div class="otp-box">
                        <div>Your OTP Code:</div>
                        <div class="otp-code">%s</div>
                    </div>
                    
                    <div class="warning">
                        ⚠️ <strong>Important:</strong><br>
                        • This OTP is valid for <strong>10 minutes</strong><br>
                        • Do not share this code with anyone<br>
                        • If you didn't request this, please ignore this email
                    </div>
                    
                    <p>If you have any questions, please contact our support team.</p>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>&copy; 2026 %s. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, otp, appName);
    }

    // Optional: Simple text email fallback
    public void sendSimpleOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP - " + appName);
        message.setText(String.format(
                "Your OTP for password reset is: %s\n\n" +
                        "This OTP is valid for 10 minutes.\n" +
                        "Do not share this code with anyone.\n\n" +
                        "If you didn't request this, please ignore this email.\n\n" +
                        "Regards,\n%s Team",
                otp, appName
        ));

        mailSender.send(message);
    }
}