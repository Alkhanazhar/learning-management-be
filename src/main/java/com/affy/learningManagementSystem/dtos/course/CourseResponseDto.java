package com.affy.learningManagementSystem.dtos.course;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class CourseResponseDto {
    private UUID id;
    private String courseName;
    private String title;
    private String description;
    private String thumbnail;
    private String createdBy;
    private CourseType courseType;
}
