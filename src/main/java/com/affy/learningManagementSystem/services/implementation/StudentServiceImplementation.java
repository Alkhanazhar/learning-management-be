


package com.affy.learningManagementSystem.services.implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.dtos.student.ExternalStudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentDetailDto;
import com.affy.learningManagementSystem.dtos.student.StudentUpdateDto;
import com.affy.learningManagementSystem.models.Course;
import com.affy.learningManagementSystem.models.Student;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.StudentRepository;
import com.affy.learningManagementSystem.services.StudentService;
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
import java.util.Optional;

import com.affy.learningManagementSystem.repositories.CourseRepository;

@Service
public class StudentServiceImplementation implements StudentService {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private CourseRepository courseRepository;

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
    public Map<String, Object> createStudent(StudentAddDto request) {
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
        User admin;
        try {
            admin = adminRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + request.getAdminId()));
        } catch (IllegalArgumentException e) {
            response.put("response", e.getMessage());
            return response;
        }


        Student student = modelMapper.map(request, Student.class);
        student.setRole("STUDENT");
        student.setPassword(passwordUtils.encryptPassword(request.getPassword()));
        student.setAdmin(admin); // Assign the admin to the teacher

        student = repository.save(student);
        response.put("response", modelMapper.map(student, StudentDetailDto.class));
        return response;
    }

    @Override
    public Map<String, Object> createExternalStudent(ExternalStudentAddDto request) {
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
        Student student = modelMapper.map(request, Student.class);
        student.setRole("STUDENT");
        student.setType("EXTERNAL");
        student.setPassword(passwordUtils.encryptPassword(request.getPassword()));
        student = repository.save(student);
        response.put("response", modelMapper.map(student, StudentDetailDto.class));
        return response;
    }

    @Override
    public Map<String, Object> updateStudent(UUID id, StudentUpdateDto request) {
        Map<String, Object> response = new HashMap<>();

        // Check if the admin exists
        Student existingStudent = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        // Check if the email is already in use by another admin
        Student existingEmailUser = repository.findByEmail(request.getEmail());
        if (existingEmailUser != null && !existingEmailUser.getId().equals(id)) {
            response.put("response", "This email is already in use.");
            return response;
        }

        // Check if the phone number is already in use by another admin
        Student existingPhoneUser = repository.findByPhoneNumber(request.getPhoneNumber());
        if (existingPhoneUser != null && !existingPhoneUser.getId().equals(id)) {
            response.put("response", "This phone number is already in use.");
            return response;
        }

        // Update admin details
        modelMapper.map(request, existingStudent);
        // Save updated admin
        Student updatedStudent = repository.save(existingStudent);
        response.put("response", modelMapper.map(updatedStudent, StudentDetailDto.class));
        return response;
    }


    @Override
    public StudentDetailDto getStudentById(UUID id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id));
        return modelMapper.map(student, StudentDetailDto.class);
    }

    @Override
    public List<StudentDetailDto> getAllStudents() {
        List<Student> students = repository.findAll();
        if (students.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");
        }
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDetailDto> getAllInternalStudents() {
        List<Student> student = repository.findByType("INTERNAL");
        if (student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");
        }
        return student.stream()
                .map(students -> modelMapper.map(students, StudentDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDetailDto> getAllExternalStudents() {
        List<Student> student = repository.findByType("EXTERNAL");
        if (student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");
        }
        return student.stream()
                .map(students -> modelMapper.map(students, StudentDetailDto.class))
                .collect(Collectors.toList());
    }

    // Method to get all teachers by adminId
    @Override
    public List<StudentDetailDto> getAllStudentsByAdminId(UUID adminId) {
        List<Student> students = repository.findAllStudentsByAdminId(adminId);
        if (students.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found for this admin.");
        }
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDetailDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> authenticateStudent(LoginDto request) {
        Student student = repository.findByEmail(request.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (student != null) {
            BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(), student.getPassword());
            StudentDetailDto studentDetailDto = modelMapper.map(student, StudentDetailDto.class);
            if (result.verified) {
                String token = jwtUtil.generateTokenForStudent(student.getEmail(), studentDetailDto);
                response.put("response", token);
            } else {
                response.put("response", "Invalid Password");
            }
        } else {
            response.put("response", "Student Not Found");
        }
        return response;
    }




    @Override
    public void deleteStudent(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Student not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean resetPassword(UUID id, String oldPassword, String newPassword) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID: " + id));

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), student.getPassword());

        if (!result.verified) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        student.setPassword(passwordUtils.encryptPassword(newPassword));
        repository.save(student);
        return true;
    }

    @Override
    public boolean toggleCourseInWishlist(UUID studentId, UUID courseId) {
        Optional<Student> studentOpt = repository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if (studentOpt.isPresent() && courseOpt.isPresent()) {
            Student student = studentOpt.get();
            Course course = courseOpt.get();

            // Check if the course is already in the wishlist
            if (student.getWishlist().contains(course)) {
                // If it is, remove the course
                student.getWishlist().remove(course);
                repository.save(student);
                return false; // Indicate that the course was removed
            } else {
                // If not, add the course
                student.getWishlist().add(course);
                repository.save(student);
                return true; // Indicate that the course was added
            }
        } else {
            throw new RuntimeException("Student or Course not found");
        }
    }

    @Override
    public List<CourseResponseDto> getWishlistByStudentId(UUID studentId) {
        Optional<Student> studentOpt = repository.findById(studentId);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<Course> wishlist = student.getWishlist();

           // Convert List<Course> to List<CourseResponseDto>
            return wishlist.stream()
                    .map(course -> modelMapper.map(course, CourseResponseDto.class))
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Course not found");
        }
    }

}



