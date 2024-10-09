package com.affy.learningManagementSystem.controllers;

import com.affy.learningManagementSystem.dtos.forgetPassword.ForgetPasswordRequestDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.VerifyOtpRequestDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherAddDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherUpdateDto;
import com.affy.learningManagementSystem.models.Student;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.TeacherRepository;
import com.affy.learningManagementSystem.services.EmailService;
import com.affy.learningManagementSystem.services.OtpService;
import com.affy.learningManagementSystem.services.TeacherService;
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
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;


    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordUtils passwordUtils;


    @Operation(summary = "create teacher")
    @PostMapping("/api/teacher")
    public ResponseEntity<Map<String, Object>> createTeacher(@RequestBody TeacherAddDto request) {
        Map<String, Object> response = teacherService.createTeacher(request);
        /* Storing value from the response. */
        Object value = response.values().iterator().next();
        /* If request is successful. */
        if (value.getClass() == TeacherDetailDto.class) {
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }

    @Operation(summary = "update existing teacher")
    @PutMapping("/api/teacher/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable UUID id, @RequestBody TeacherUpdateDto request) {
        Map<String, Object> response = teacherService.updateTeacher(id, request);
        Object value = response.values().iterator().next();

        if (value.getClass() == TeacherDetailDto.class) {
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }


    @Operation(summary = "get teacher by ID")
    @GetMapping("/api/teacher/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable UUID id) {
        try {
            TeacherDetailDto principle = teacherService.getTeacherById(id);
            return ResponseEntity.ok(principle);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Teacher not found with ID: " + id));
        }
    }

    @Operation(summary = "Get all teachers ")
    @GetMapping("/api/teacher")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<TeacherDetailDto> teachers = teacherService.getAllTeachers();
            return ResponseEntity.ok(teachers);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }



    @Operation(summary = "delete teacher")
    @DeleteMapping("/api/teacher/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable UUID id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset the Teacher's password")
    @PatchMapping("/api/teacher/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable UUID id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            teacherService.resetPassword(id, oldPassword, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @Operation(summary = "send otp forget password teacher")
    @PostMapping("/api/teacher/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequestDto request) {
        Teacher teacher = teacherRepository.findByEmail(request.getEmail());

        if (teacher != null) {
            String otp = otpService.generateOtp();
            otpService.storeOtp(request.getEmail(), otp);
            emailService.sendOtp(request.getEmail(), otp);
            return ResponseEntity.ok("OTP sent to your email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Teacher not found.");
        }
    }

    @Operation(summary = "Verify OTP and reset password of teacher")
    @PostMapping("/api/teacher/verify-otp")
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Teacher not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

}