package com.smart_finance_manager_backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendOtpEmail(String toEmail, String otp) {
        String message = String.format(
                "\n========================================\n" +
                        "EMAIL TO: %s\n" +
                        "SUBJECT: Smart Finance - Password Reset OTP\n" +
                        "BODY: Your OTP is: %s (valid for 15 minutes)\n" +
                        "========================================\n", toEmail, otp);

        System.out.println(message);
    }
}