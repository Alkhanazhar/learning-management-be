package com.affy.learningManagementSystem.models;


import com.affy.learningManagementSystem.dtos.video.Status;
import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Ebook {
    
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String title;
    private String author;
    private String category;
    
    // Path to the ebook file
    private String filePath;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private UUID courseId;

    // Timestamps for creation and update
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
}

