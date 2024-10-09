package com.affy.learningManagementSystem.dtos.admin;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
public class AdminAddDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String SchoolName;
    private String username;
    private String password;
}
