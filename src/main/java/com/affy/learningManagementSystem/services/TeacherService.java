package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherAddDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherUpdateDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TeacherService {
    Map<String, Object> createTeacher(TeacherAddDto teacherAddDto);
    Map<String, Object> updateTeacher(UUID id, TeacherUpdateDto teacherAddDto);
    TeacherDetailDto getTeacherById(UUID id);
    List<TeacherDetailDto> getAllTeachers();
    List<TeacherDetailDto> getAllTeachersByAdminId(UUID adminId);
    void deleteTeacher(UUID id);
    Map<String, Object> authenticateTeacher(LoginDto request);
    public boolean resetPassword(UUID id, String oldPassword, String newPassword);
}
