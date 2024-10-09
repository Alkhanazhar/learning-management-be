package com.affy.learningManagementSystem.dtos.teacher;

import lombok.Data;

import java.util.UUID;

@Data
public class TeacherDetailDto {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String role;
    private String createdBy;
    private UUID adminId; // Add adminId
}
