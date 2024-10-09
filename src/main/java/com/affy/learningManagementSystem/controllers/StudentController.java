package com.affy.learningManagementSystem.controllers;

import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.ForgetPasswordRequestDto;
import com.affy.learningManagementSystem.dtos.forgetPassword.VerifyOtpRequestDto;
import com.affy.learningManagementSystem.dtos.student.ExternalStudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentDetailDto;
import com.affy.learningManagementSystem.dtos.student.StudentUpdateDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherAddDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.models.Course;
import com.affy.learningManagementSystem.models.Student;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.StudentRepository;
import com.affy.learningManagementSystem.services.EmailService;
import com.affy.learningManagementSystem.services.OtpService;
import com.affy.learningManagementSystem.services.StudentService;
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
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;


    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordUtils passwordUtils;


    @Operation(summary = "create student")
    @PostMapping("/api/student")
    public ResponseEntity<Map<String, Object>> createStudent(@RequestBody StudentAddDto request) {
        Map<String, Object> response = studentService.createStudent(request);
        /* Storing value from the response. */
        Object value = response.values().iterator().next();
        /* If request is successful. */
        if (value.getClass() == StudentDetailDto.class) {
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }

    @Operation(summary = "create external student")
    @PostMapping("/api/student-external")
    public ResponseEntity<Map<String, Object>> createExternalStudent(@RequestBody ExternalStudentAddDto request) {
        Map<String, Object> response = studentService.createExternalStudent(request);
        /* Storing value from the response. */
        Object value = response.values().iterator().next();
        /* If request is successful. */
        if (value.getClass() == StudentDetailDto.class) {
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }

    @Operation(summary = "update existing student")
    @PutMapping("/api/student/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable UUID id, @RequestBody StudentUpdateDto request) {
        Map<String, Object> response = studentService.updateStudent(id, request);
        Object value = response.values().iterator().next();

        if (value.getClass() == StudentDetailDto.class) {
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.IM_USED).body(response);
        }
    }


    @Operation(summary = "get student by ID")
    @GetMapping("/api/student/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable UUID id) {
        try {
            StudentDetailDto student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "student not found with ID: " + id));
        }
    }

    @Operation(summary = "Get all students")
    @GetMapping("/api/student")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<StudentDetailDto> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }

    @Operation(summary = "Get all external students")
    @GetMapping("/api/student/external")
    public ResponseEntity<?> getAllExternalStudents() {
        try {
            List<StudentDetailDto> students = studentService.getAllExternalStudents();
            return ResponseEntity.ok(students);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }


    @Operation(summary = "Get all internal students")
    @GetMapping("/api/student/internal")
    public ResponseEntity<?> getAllInternalStudents() {
        try {
            List<StudentDetailDto> students = studentService.getAllInternalStudents();
            return ResponseEntity.ok(students);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", ex.getReason()));
        }
    }



    @Operation(summary = "delete student")
    @DeleteMapping("/api/student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset the Student's password")
    @PatchMapping("/api/student/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable UUID id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            studentService.resetPassword(id, oldPassword, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @Operation(summary = "send otp forget password student")
    @PostMapping("/api/student/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequestDto request) {
        Student student = studentRepository.findByEmail(request.getEmail());

        if (student != null) {
            String otp = otpService.generateOtp();
            otpService.storeOtp(request.getEmail(), otp);
            emailService.sendOtp(request.getEmail(), otp);
            return ResponseEntity.ok("OTP sent to your email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
        }
    }

    @Operation(summary = "Verify OTP and reset password of student")
    @PostMapping("/api/student/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDto request) {
        String cachedOtp = otpService.getOtp(request.getEmail());

        if (cachedOtp != null && cachedOtp.equals(request.getOtp())) {
            Student student = studentRepository.findByEmail(request.getEmail());

            if (student != null) {
                student.setPassword(passwordUtils.encryptPassword(request.getNewPassword()));
                studentRepository.save(student);
                otpService.invalidateOtp(request.getEmail());
                return ResponseEntity.ok("Password reset successful.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    @PostMapping("/{studentId}/wishlist/{courseId}/toggle")
    public ResponseEntity<?> toggleCourseInWishlist(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId) {
        try {
            boolean isAdded = studentService.toggleCourseInWishlist(studentId, courseId);
            if (isAdded) {
                return ResponseEntity.ok("Course added to wishlist");
            } else {
                return ResponseEntity.ok("Course removed from wishlist");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/api/student/{studentId}/wishlist")
    public ResponseEntity<?> getWishlistByStudentId(@PathVariable UUID studentId) {
        try {
            List<CourseResponseDto> wishlist = studentService.getWishlistByStudentId(studentId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

}
