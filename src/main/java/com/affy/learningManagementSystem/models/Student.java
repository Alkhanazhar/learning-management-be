package com.affy.learningManagementSystem.models;

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
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(length = 128)
    private String name;

    @Column(length = 512)
    private String email;

    @Column(length = 15)
    private String phoneNumber;


    @Column(length = 256)
    private String username;

    @Column(length = 70)
    private String password;

    @Column(length = 50)
    private String role;  //field for role in future we change datatype of this field

    @Column(length = 50)
    private String type; //field for type there are two types of students internal and external

    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin; // Reference to Admin

    @ManyToMany
    @JoinTable(
        name = "student_wishlist",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> wishlist;

    @Column
    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;

    @Column
    @UpdateTimestamp(source = SourceType.DB)
    private Date updatedAt;
}
