package com.affy.learningManagementSystem.repositories;

import com.affy.learningManagementSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuperAdminRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    User findByRole(String role);
}
