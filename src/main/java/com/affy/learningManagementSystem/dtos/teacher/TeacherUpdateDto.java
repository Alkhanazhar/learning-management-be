package com.affy.learningManagementSystem.dtos.teacher;

import lombok.Data;

import java.util.UUID;

@Data
public class TeacherUpdateDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
}
