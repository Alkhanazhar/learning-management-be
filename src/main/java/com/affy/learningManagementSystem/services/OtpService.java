package com.affy.learningManagementSystem.services;

public interface OtpService {

    public String generateOtp();
    public void storeOtp(String email, String otp);
    public String getOtp(String email);
    public void invalidateOtp(String email);
}
