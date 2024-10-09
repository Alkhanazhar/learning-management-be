package com.affy.learningManagementSystem.services.implementation;


import com.affy.learningManagementSystem.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;



@Service
public class EmailServiceImplementation implements EmailService {

    @Autowired
    private JavaMailSender mailSender;



    @Override
    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp);
        mailSender.send(message);
    }

    public void sendMail(String toEmail, String messageMail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + messageMail);
        mailSender.send(message);
    }

}

