package com.affy.learningManagementSystem.services.implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.affy.learningManagementSystem.dtos.admin.AdminAddDto;
import com.affy.learningManagementSystem.dtos.admin.AdminDetailDto;
import com.affy.learningManagementSystem.dtos.admin.AdminUpdateDto;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.services.AdminService;
import com.affy.learningManagementSystem.utils.JwtUtil;
import com.affy.learningManagementSystem.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImplementation implements AdminService {

    @Autowired
    private AdminRepository repository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private ModelMapper modelMapper;


    /* Save details received from CompanyAddDto into the database
     * NOTE: Request validators are still unimplemented in the backend.*/
    @Override
    public Map<String, Object> createAdmin(AdminAddDto request) {
        /* Map storing response. */
        Map<String, Object> response = new HashMap<>();
        /* Check if the email is already in use */
        if (repository.findByEmail(request.getEmail()) != null) {
            response.put("response", "This email already exists");
            return response;
        }
        /*  Check if the phone number is already in use */
        if (repository.findByPhoneNumber(request.getPhoneNumber()) != null) {
            response.put("response", "This phone number exists.");
            return response;
        }
        User user = modelMapper.map(request, User.class);
        user.setRole("ADMIN");
        user.setPassword(passwordUtils.encryptPassword(request.getPassword()));
        user = repository.save(user);
        response.put("response", modelMapper.map(user, AdminDetailDto.class));
        return response;
    }



    @Override
    public Map<String, Object> updateAdmin(UUID id, AdminUpdateDto request) {
        Map<String, Object> response = new HashMap<>();

        // Check if the admin exists
        User existingAdmin = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + id));

        // Check if the email is already in use by another admin
        User existingEmailUser = repository.findByEmail(request.getEmail());
        if (existingEmailUser != null && !existingEmailUser.getId().equals(id)) {
            response.put("response", "This email is already in use.");
            return response;
        }

        // Check if the phone number is already in use by another admin
        User existingPhoneUser = repository.findByPhoneNumber(request.getPhoneNumber());
        if (existingPhoneUser != null && !existingPhoneUser.getId().equals(id)) {
            response.put("response", "This phone number is already in use.");
            return response;
        }

        // Update admin details
        modelMapper.map(request, existingAdmin);

        // Save updated admin
        User updatedAdmin = repository.save(existingAdmin);
        response.put("response", modelMapper.map(updatedAdmin, AdminDetailDto.class));
        return response;
    }


    @Override
    public AdminDetailDto getAdminById(UUID id) {
        User admin = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with ID: " + id));
        return modelMapper.map(admin, AdminDetailDto.class);
    }

    @Override
    public List<AdminDetailDto> getAllAdmins() {
        List<User> admins = repository.findByRole("ADMIN");
        if (admins.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No admins found");
        }
        return admins.stream()
                .map(admin -> modelMapper.map(admin, AdminDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAdmin(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Admin not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean resetPassword(UUID id, String oldPassword, String newPassword) {
        User admin = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Admin ID: " + id));

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), admin.getPassword());

        if (!result.verified) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        admin.setPassword(passwordUtils.encryptPassword(newPassword));
        repository.save(admin);
        return true;
    }

    @Autowired
    private JwtUtil jwtUtil;


}
