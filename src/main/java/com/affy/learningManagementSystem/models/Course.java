package com.affy.learningManagementSystem.models;

import com.affy.learningManagementSystem.dtos.course.CourseType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor // This is the no-args constructor required by JPA
@AllArgsConstructor // This generates a constructor with all fields as parameters
public class Course {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    private String courseName;
    private String title;
    private String description;
    private String thumbnail;
    private String createdBy;


    @Enumerated(EnumType.STRING)
    private CourseType courseType = CourseType.PRIVATE; // Default value set to PRIVATE

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; // Relationship with Teacher

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin; // Relationship with Admin

    // Custom constructor to match your requirement
    public Course(String courseName, String title, String description, String thumbnail, CourseType courseType, Teacher teacher, User admin) {
        this.courseName = courseName;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.courseType = courseType;
        this.teacher = teacher;
        this.admin = admin;

    }
}
