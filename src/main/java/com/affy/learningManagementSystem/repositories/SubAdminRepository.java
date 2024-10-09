package com.affy.learningManagementSystem.repositories;

import com.affy.learningManagementSystem.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubAdminRepository extends JpaRepository<Teacher, UUID> {
    Teacher findByEmail(String email);
    // phoneNumber unique check
    Teacher findByPhoneNumber(String phoneNumber);
    // Add this method to fetch subadmins with 'SUB_ADMIN' role by adminId
    List<Teacher> findAllByAdminIdAndRole(UUID adminId, String role);
    // Add this method to find all subadmins with the role 'SUB_ADMIN'
    List<Teacher> findAllByRole(String role);
}
