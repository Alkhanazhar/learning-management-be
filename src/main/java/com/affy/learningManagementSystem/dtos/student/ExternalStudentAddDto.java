package com.affy.learningManagementSystem.dtos.student;

import lombok.Data;

@Data
public class ExternalStudentAddDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String subject;
    private String username;
    private String password;

}
