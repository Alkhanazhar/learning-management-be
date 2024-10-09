package com.affy.learningManagementSystem.services.implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.dtos.superAdmin.SuperAdminDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherAddDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherUpdateDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.TeacherRepository;
import com.affy.learningManagementSystem.services.TeacherService;
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
public class TeacherServiceImplementation implements TeacherService {

    @Autowired
    private TeacherRepository repository;

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
    public Map<String, Object> createTeacher(TeacherAddDto request) {
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
        Teacher teacher = modelMapper.map(request, Teacher.class);
        teacher.setRole("TEACHER");
        teacher.setPassword(passwordUtils.encryptPassword(request.getPassword()));
        teacher.setAdmin(admin); // Assign the admin to the teacher

        teacher = repository.save(teacher);
        response.put("response", modelMapper.map(teacher, TeacherDetailDto.class));
        return response;
    }

    @Override
    public Map<String, Object> updateTeacher(UUID id, TeacherUpdateDto request) {
        Map<String, Object> response = new HashMap<>();

        // Check if the admin exists
        Teacher existingTeacher = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));

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
        modelMapper.map(request, existingTeacher);
        // Save updated teacher
        Teacher updatedTeacher= repository.save(existingTeacher);
        response.put("response", modelMapper.map(updatedTeacher, TeacherDetailDto.class));
        return response;
    }


    @Override
    public TeacherDetailDto getTeacherById(UUID id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found with ID: " + id));
        return modelMapper.map(teacher, TeacherDetailDto.class);
    }

    @Override
    public List<TeacherDetailDto> getAllTeachers() {
        List<Teacher> teachers = repository.findAllByRole("TEACHER");
        if (teachers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No teachers found");
        }
        return teachers.stream()
                .map(teacher -> {
                    TeacherDetailDto teacherDetailDto = modelMapper.map(teacher, TeacherDetailDto.class);
                    return teacherDetailDto;
                })
                .collect(Collectors.toList());
    }


    // Method to get all teachers by adminId
    @Override
    public List<TeacherDetailDto> getAllTeachersByAdminId(UUID adminId) {
        List<Teacher> teachers = repository.findAllByAdminIdAndRole(adminId,"TEACHER");
        if (teachers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No teachers found for this admin.");
        }
        return teachers.stream()
                .map(teacher -> modelMapper.map(teacher, TeacherDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> authenticateTeacher(LoginDto request) {
        Teacher teacher = repository.findByEmail(request.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (teacher != null) {
            BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(), teacher.getPassword());
            TeacherDetailDto teacherDetailDto = modelMapper.map(teacher, TeacherDetailDto.class);
            if (result.verified) {
                String token = jwtUtil.generateTokenForTeacher(teacher.getEmail(), teacherDetailDto);
                response.put("response", token);
            } else {
                response.put("response", "Invalid Password");
            }
        } else {
            response.put("response", "Teacher Not Found");
        }
        return response;
    }



    @Override
    public void deleteTeacher(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean resetPassword(UUID id, String oldPassword, String newPassword) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Teacher ID: " + id));

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), teacher.getPassword());

        if (!result.verified) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        teacher.setPassword(passwordUtils.encryptPassword(newPassword));
        repository.save(teacher);
        return true;
    }

}
