package com.affy.learningManagementSystem.repositories;

import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Teacher findByEmail(String email);
    Teacher findByPhoneNumber(String phoneNumber); // phoneNumber unique check
    // Add this method to fetch teachers with 'TEACHER' role by adminId
    List<Teacher> findAllByAdminIdAndRole(UUID adminId, String role);
    // Add this method to find all teachers with the role 'TEACHER'
    List<Teacher> findAllByRole(String role);}
