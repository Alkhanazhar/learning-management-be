package com.affy.learningManagementSystem.controllers;

import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.repositories.SuperAdminRepository;
import com.affy.learningManagementSystem.services.StudentService;
import com.affy.learningManagementSystem.services.SuperAdminService;
import com.affy.learningManagementSystem.services.TeacherService;
import com.affy.learningManagementSystem.utils.PasswordUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    SuperAdminService superAdminService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    StudentService studentService;

    @Autowired
    private PasswordUtils passwordUtils;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);


    @Operation(summary = "Authenticate SuperAdmin or Admin with the given email address and password.")
    @PostMapping("/api/login")
    public ResponseEntity<Map<String, Object>> authenticateSuperAdmin(@RequestBody LoginDto request) {
        Map<String, Object> response = superAdminService.authenticateSuperAdmin(request);
        if (response.get("response") == "Invalid Password") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        else if (response.get("response") == "User Not Found") {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {
            log.info("Successfully authenticated: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @Operation(summary = "Authenticate Teacher and SubAdmin with the given email address and password.")
    @PostMapping("/api/teacher/login")
    public ResponseEntity<Map<String, Object>> authenticateTeacher(@RequestBody LoginDto request) {
        Map<String, Object> response = teacherService.authenticateTeacher(request);
        if (response.get("response") == "Invalid Password") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        else if (response.get("response") == "Teacher Not Found") {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {
            log.info("Successfully authenticated: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @Operation(summary = "Authenticate Student with the given email address and password.")
    @PostMapping("/api/student/login")
    public ResponseEntity<Map<String, Object>> authenticateStudent(@RequestBody LoginDto request) {
        Map<String, Object> response = studentService.authenticateStudent(request);
        if (response.get("response") == "Invalid Password") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        else if (response.get("response") == "Student Not Found") {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {
            log.info("Successfully authenticated: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }


}
