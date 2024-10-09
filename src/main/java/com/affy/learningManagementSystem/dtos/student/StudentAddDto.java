package com.affy.learningManagementSystem.dtos.student;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentAddDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String type;
    private String password;
    private String createdBy;
    private UUID adminId; // Add adminId

}
