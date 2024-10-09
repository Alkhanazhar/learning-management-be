package com.affy.learningManagementSystem.dtos.student;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentDetailDto {

    private UUID studentId;
    private String name;
    private String email;
    private String phoneNumber;
    private String type;
    private String role;
    private String username;
    private String createdBy;
    private UUID adminId; // Add adminId


}
