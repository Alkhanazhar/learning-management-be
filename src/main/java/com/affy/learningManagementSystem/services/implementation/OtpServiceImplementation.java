package com.affy.learningManagementSystem.services.implementation;


import com.affy.learningManagementSystem.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpServiceImplementation implements OtpService {


    private final CacheManager cacheManager;

    @Autowired
    public OtpServiceImplementation(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Generates a 6-digit OTP
    @Override
    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // Stores the OTP in the cache
    @Override
    public void storeOtp(String email, String otp) {
        cacheManager.getCache("otpCache").put(email, otp);
    }

    // Retrieves the OTP from the cache
    @Override
    public String getOtp(String email) {
        return cacheManager.getCache("otpCache").get(email, String.class);
    }

    // Invalidates the OTP in the cache
    @Override
    public void invalidateOtp(String email) {
        cacheManager.getCache("otpCache").evict(email);
    }
}

