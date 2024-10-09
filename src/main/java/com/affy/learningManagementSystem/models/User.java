package com.affy.learningManagementSystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(length = 128)
    private String name;

    @Column(length = 128)
    private String SchoolName;

    @Column(length = 512)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 256)
    private String username;

    @Column(length = 70)
    private String password;

    @Column(length = 50)
    private String role;  // 'ADMIN' or 'SUPER_ADMIN'

    private String createdBy;


    @JsonManagedReference
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Teacher> teachers; // List of teachers under this admin


    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses; // One-to-many relationship with Course

    @Column
    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;

    @Column
    @UpdateTimestamp(source = SourceType.DB)
    private Date updatedAt;


}
