package com.affy.learningManagementSystem.dtos.student;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentUpdateDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String type;
}
