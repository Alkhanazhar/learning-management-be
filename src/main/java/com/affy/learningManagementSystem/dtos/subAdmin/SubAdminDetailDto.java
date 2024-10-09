package com.affy.learningManagementSystem.dtos.subAdmin;

import lombok.Data;

import java.util.UUID;

@Data
public class SubAdminDetailDto {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String role;
    private UUID adminId; // Add adminId
}
