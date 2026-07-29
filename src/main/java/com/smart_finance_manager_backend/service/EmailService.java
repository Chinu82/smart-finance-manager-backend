package com.smart_finance_manager_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("Smart Finance - Password Reset OTP");

        message.setText(
                    """
                    Hello,
                
                    We received a request to reset your Smart Finance account password.
                
                    Your One-Time Password (OTP) is:
                
                    %s
                
                    This OTP is valid for 15 minutes.
                
                    If you did not request a password reset, please ignore this email.
                
                    Regards,
                    Smart Finance Team
                    """.formatted(otp)
            );

        mailSender.send(message);
    }
}