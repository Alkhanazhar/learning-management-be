package com.affy.learningManagementSystem.repositories;

import com.affy.learningManagementSystem.models.Student;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Student findByEmail(String email);
    Student findByPhoneNumber(String phoneNumber); // phoneNumber unique check

    // Custom method to find users with the role "ADMIN"
    List<Student> findByType(String type);

    List<Student> findAllStudentsByAdminId(UUID adminId);
}
