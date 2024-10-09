package com.affy.learningManagementSystem.repositories;

import com.affy.learningManagementSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber); // phoneNumber unique check

    // Custom method to find users with the role "ADMIN"
    List<User> findByRole(String role);

}
