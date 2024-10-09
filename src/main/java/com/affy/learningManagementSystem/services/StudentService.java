package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.dtos.student.ExternalStudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentAddDto;
import com.affy.learningManagementSystem.dtos.student.StudentDetailDto;
import com.affy.learningManagementSystem.dtos.student.StudentUpdateDto;
import com.affy.learningManagementSystem.models.Course;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentService {
    Map<String, Object> createStudent(StudentAddDto studentAddDto);
    Map<String, Object> createExternalStudent(ExternalStudentAddDto externalStudentAddDto);
    Map<String, Object> updateStudent(UUID id, StudentUpdateDto studentUpdateDto);
    StudentDetailDto getStudentById(UUID id);
    List<StudentDetailDto> getAllStudents();
    List<StudentDetailDto> getAllExternalStudents();
    List<StudentDetailDto> getAllInternalStudents();List<StudentDetailDto> getAllStudentsByAdminId(UUID adminId);
    Map<String, Object> authenticateStudent(LoginDto request);
    void deleteStudent(UUID id);
    public boolean resetPassword(UUID id, String oldPassword, String newPassword);
    boolean toggleCourseInWishlist(UUID studentId, UUID courseId); 
    List<CourseResponseDto> getWishlistByStudentId(UUID studentId);

}
