package com.affy.learningManagementSystem.models;

import java.util.List;
import java.util.UUID;

import com.affy.learningManagementSystem.dtos.video.Status;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Videos {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;
    private String title;
    private String description;

    @ElementCollection
    private List<String> videoPaths;

    private UUID courseId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

  
}
