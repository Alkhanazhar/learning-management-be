package com.affy.learningManagementSystem.services;



public interface EmailService {

    public void sendOtp(String toEmail, String otp);
    public void sendMail(String email, String message);
}
