package com.affy.learningManagementSystem.dtos.admin;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AdminDetailDto {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String SchoolName;
    private String username;
    private String role;
    private Date createdAt;
    private Date updatedAt;

}
