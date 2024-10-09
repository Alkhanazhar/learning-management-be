package com.affy.learningManagementSystem.controllers;

import com.affy.learningManagementSystem.dtos.admin.AdminAddDto;
import com.affy.learningManagementSystem.dtos.admin.AdminDetailDto;
import com.affy.learningManagementSystem.dtos.admin.AdminUpdateDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.ForgetPasswordRequestDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.VerifyOtpRequestDto;
import com.affy.learningManagementSystem.dtos.student.StudentDetailDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.services.*;
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
//@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private AdminRepository adminRepository;


    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordUtils passwordUtils;

    @Operation(summary = "create admin")
    @PostMapping("/api/admin")
    public ResponseEntity<Map<String, Object>> createAdmin(@RequestBody AdminAddDto request) {
        Map<String, Object> response = adminService.createAdmin(request);
        /* Storing value from the response. */
        Object value = response.values().iterator().next();
        /* If request is successful. */
        if (value.getClass() == AdminDetailDto.class) {
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }

    @Operation(summary = "update existing admin")
    @PutMapping("/api/admin/{id}")
    public ResponseEntity<Map<String, Object>> updateAdmin(@PathVariable UUID id, @RequestBody AdminUpdateDto request) {
        Map<String, Object> response = adminService.updateAdmin(id, request);
        Object value = response.values().iterator().next();

        if (value.getClass() == AdminDetailDto.class) {
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }


    @Operation(summary = "Get school by ID")
    @GetMapping("/api/admin/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable UUID id) {
        try {
            AdminDetailDto admin = adminService.getAdminById(id);
            return ResponseEntity.ok(admin);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Admin not found with ID: " + id));
        }
    }

    @Operation(summary = "Get all schools ")
    @GetMapping("/api/admin")
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<AdminDetailDto> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }

    // Endpoint to get all teachers by adminId
    @Operation(summary = "Get all teachers by Admin ID")
    @GetMapping("/api/admin/teacher/{adminId}")
    public ResponseEntity<List<TeacherDetailDto>> getAllTeachersByAdminId(@PathVariable UUID adminId) {
        List<TeacherDetailDto> teachers = teacherService.getAllTeachersByAdminId(adminId);
        return ResponseEntity.ok(teachers);
    }

    // Endpoint to get all sub-admins by adminId
    @Operation(summary = "Get all subadmins by Admin ID")
    @GetMapping("/api/admin/subAdmins/{adminId}")
    public ResponseEntity<List<SubAdminDetailDto>> getAllSubAdminsByAdminId(@PathVariable UUID adminId) {
        List<SubAdminDetailDto> subAdmins = subAdminService.getAllSubAdminsByAdminId(adminId);
        return ResponseEntity.ok(subAdmins);
    }


    // Endpoint to get all teachers by adminId
    @Operation(summary = "Get all student by Admin ID")
    @GetMapping("/api/admin/student/{adminId}")
    public ResponseEntity<List<StudentDetailDto>> getAllStudentsByAdminId(@PathVariable UUID adminId) {
        List<StudentDetailDto> students = studentService.getAllStudentsByAdminId(adminId);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "delete admin")
    @DeleteMapping("/api/admin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset the Admin's password")
    @PatchMapping("/api/admin/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable UUID id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            adminService.resetPassword(id, oldPassword, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @Operation(summary = "send otp forget password admin")
    @PostMapping("/api/admin/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequestDto request) {
        User admin = adminRepository.findByEmail(request.getEmail());

        if (admin != null) {
            String otp = otpService.generateOtp();
            otpService.storeOtp(request.getEmail(), otp);
            emailService.sendOtp(request.getEmail(), otp);
            return ResponseEntity.ok("OTP sent to your email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
        }
    }

    @Operation(summary = "Verify OTP and reset password of admin")
    @PostMapping("/api/admin/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDto request) {
        String cachedOtp = otpService.getOtp(request.getEmail());

        if (cachedOtp != null && cachedOtp.equals(request.getOtp())) {
            User admin = adminRepository.findByEmail(request.getEmail());

            if (admin != null) {
                admin.setPassword(passwordUtils.encryptPassword(request.getNewPassword()));
                adminRepository.save(admin);
                otpService.invalidateOtp(request.getEmail());
                return ResponseEntity.ok("Password reset successful.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }



}
