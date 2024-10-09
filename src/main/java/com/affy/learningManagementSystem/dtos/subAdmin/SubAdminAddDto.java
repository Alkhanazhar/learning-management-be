package com.affy.learningManagementSystem.dtos.subAdmin;

import lombok.Data;

import java.util.UUID;

@Data
public class SubAdminAddDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private UUID adminId; // Add adminId

}
