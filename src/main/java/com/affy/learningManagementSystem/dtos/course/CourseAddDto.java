package com.affy.learningManagementSystem.dtos.course;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseAddDto {
    private String courseName;
    private String title;
    private String description;
    private MultipartFile thumbnail;
    private CourseType courseType;
    private String createdBy;
    private UUID teacherId; // Field to reference the teacher
    private UUID adminId; // Field to reference the admin


}
