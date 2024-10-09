package com.affy.learningManagementSystem.services.implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminAddDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminDetailDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminUpdateDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.SubAdminRepository;
import com.affy.learningManagementSystem.services.SubAdminService;
import com.affy.learningManagementSystem.utils.JwtUtil;
import com.affy.learningManagementSystem.utils.PasswordUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubAdminServiceImplementation implements SubAdminService {

    @Autowired
    private SubAdminRepository repository;

    @Autowired
    private AdminRepository adminRepository; // To find admin by id

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private JwtUtil jwtUtil;

    /* Save details received from CompanyAddDto into the database
     * NOTE: Request validators are still unimplemented in the backend.*/
    @Override
    public Map<String, Object> createSubAdmin(SubAdminAddDto request) {
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

        // Find the admin by id
        User admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with ID: " + request.getAdminId()));


        // Create and save the teacher
        Teacher subAdmin = modelMapper.map(request, Teacher.class);
        subAdmin.setRole("SUB_ADMIN");
        subAdmin.setPassword(passwordUtils.encryptPassword(request.getPassword()));
        subAdmin.setAdmin(admin); // Assign the admin to the teacher

        subAdmin = repository.save(subAdmin);
        response.put("response", modelMapper.map(subAdmin, SubAdminDetailDto.class));
        return response;
    }

    @Override
    public Map<String, Object> updateSubAdmin(UUID id, SubAdminUpdateDto request) {
        Map<String, Object> response = new HashMap<>();

        // Check if the admin exists
        Teacher existingSubAdmin = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubAdmin not found with ID: " + id));

        // Check if the email is already in use by another teacher
        Teacher existingEmailUser = repository.findByEmail(request.getEmail());
        if (existingEmailUser != null && !existingEmailUser.getId().equals(id)) {
            response.put("response", "This email is already in use.");
            return response;
        }

        // Check if the phone number is already in use by another teacher
        Teacher existingPhoneUser = repository.findByPhoneNumber(request.getPhoneNumber());
        if (existingPhoneUser != null && !existingPhoneUser.getId().equals(id)) {
            response.put("response", "This phone number is already in use.");
            return response;
        }

        // Update admin details
        modelMapper.map(request, existingSubAdmin);
        // Save updated teacher
        Teacher updatedSubAdmin= repository.save(existingSubAdmin);
        response.put("response", modelMapper.map(updatedSubAdmin, TeacherDetailDto.class));
        return response;
    }


    @Override
    public SubAdminDetailDto getSubAdminById(UUID id) {
        Teacher subAdmin = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found with ID: " + id));
        return modelMapper.map(subAdmin, SubAdminDetailDto.class);
    }



    // Method to get all teachers by adminId
    @Override
    public List<SubAdminDetailDto> getAllSubAdminsByAdminId(UUID adminId) {
        List<Teacher> subAdmins = repository.findAllByAdminIdAndRole(adminId, "SUB_ADMIN");
        if (subAdmins.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No subAdmins found for this admin.");
        }
        return subAdmins.stream()
                .map(subAdmin -> modelMapper.map(subAdmin, SubAdminDetailDto.class))
                .collect(Collectors.toList());
    }



    @Override
    public List<SubAdminDetailDto> getAllSubAdminsByRole() {
        // Fetch all teachers with the role 'SUB_ADMIN'
        List<Teacher> subAdmins = repository.findAllByRole("SUB_ADMIN");
        if (subAdmins.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No subAdmins found");
        }
        return subAdmins.stream()
                .map(subAdmin -> modelMapper.map(subAdmin, SubAdminDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubAdmin(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("SubAdmin not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean resetPassword(UUID id, String oldPassword, String newPassword) {
        Teacher subAdmin = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid SubAdmin ID: " + id));

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), subAdmin.getPassword());

        if (!result.verified) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        subAdmin.setPassword(passwordUtils.encryptPassword(newPassword));
        repository.save(subAdmin);
        return true;
    }

}
