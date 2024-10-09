package com.affy.learningManagementSystem.controllers;

import com.affy.learningManagementSystem.dtos.forgetPassword.ForgetPasswordRequestDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.VerifyOtpRequestDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminAddDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminDetailDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminUpdateDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.repositories.TeacherRepository;
import com.affy.learningManagementSystem.services.EmailService;
import com.affy.learningManagementSystem.services.OtpService;
import com.affy.learningManagementSystem.services.SubAdminService;
import com.affy.learningManagementSystem.utils.PasswordUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class SubAdminController {

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private TeacherRepository teacherRepository;


    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordUtils passwordUtils;


    @Operation(summary = "create subadmin")
    @PostMapping("/api/subadmin")
    public ResponseEntity<Map<String, Object>> createSubAdmin(@RequestBody SubAdminAddDto request) {
        Map<String, Object> response = subAdminService.createSubAdmin(request);
        /* Storing value from the response. */
        Object value = response.values().iterator().next();
        /* If request is successful. */
        if (value.getClass() == SubAdminDetailDto.class) {
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }

    @Operation(summary = "update existing subadmin")
    @PutMapping("/api/subadmin/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable UUID id, @RequestBody SubAdminUpdateDto request) {
        Map<String, Object> response = subAdminService.updateSubAdmin(id, request);
        Object value = response.values().iterator().next();

        if (value.getClass() == SubAdminDetailDto.class) {
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }


    @Operation(summary = "get subadmin by ID")
    @GetMapping("/api/subadmin/{id}")
    public ResponseEntity<?> getSubAdminById(@PathVariable UUID id) {
        try {
            SubAdminDetailDto principle = subAdminService.getSubAdminById(id);
            return ResponseEntity.ok(principle);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "SubAdmin not found with ID: " + id));
        }
    }

    @Operation(summary = "Get all subadmins ")
    @GetMapping("/api/subadmin")
    public ResponseEntity<?> getAllSubAdmins() {
        try {
            List<SubAdminDetailDto> teachers = subAdminService.getAllSubAdminsByRole();
            return ResponseEntity.ok(teachers);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }



    @Operation(summary = "delete subadmin")
    @DeleteMapping("/api/subadmin/{id}")
    public ResponseEntity<Void> deleteSubAdmin(@PathVariable UUID id) {
        subAdminService.deleteSubAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset the subadmin's password")
    @PatchMapping("/api/subadmin/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable UUID id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            subAdminService.resetPassword(id, oldPassword, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @Operation(summary = "send otp forget password subadmin")
    @PostMapping("/api/subadmin/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequestDto request) {
        Teacher teacher = teacherRepository.findByEmail(request.getEmail());

        if (teacher != null) {
            String otp = otpService.generateOtp();
            otpService.storeOtp(request.getEmail(), otp);
            emailService.sendOtp(request.getEmail(), otp);
            return ResponseEntity.ok("OTP sent to your email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SubAdmin not found.");
        }
    }

    @Operation(summary = "Verify OTP and reset password of subadmin")
    @PostMapping("/api/subadmin/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDto request) {
        String cachedOtp = otpService.getOtp(request.getEmail());

        if (cachedOtp != null && cachedOtp.equals(request.getOtp())) {
            Teacher teacher = teacherRepository.findByEmail(request.getEmail());

            if (teacher != null) {
                teacher.setPassword(passwordUtils.encryptPassword(request.getNewPassword()));
                teacherRepository.save(teacher);
                otpService.invalidateOtp(request.getEmail());
                return ResponseEntity.ok("Password reset successful.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SubAdmin not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

}