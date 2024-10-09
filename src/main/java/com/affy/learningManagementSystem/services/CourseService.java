package com.affy.learningManagementSystem.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.affy.learningManagementSystem.dtos.course.CourseAddDto;
import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import org.springframework.web.multipart.MultipartFile;

import com.affy.learningManagementSystem.dtos.course.CourseType;
import com.affy.learningManagementSystem.models.Course;

public interface CourseService {
    CourseResponseDto createCourse(CourseAddDto courseAddDto) throws IOException; // Updated method signature
    List<CourseResponseDto> getAllCourses();
    CourseResponseDto getCourseById(UUID id);
    CourseResponseDto updateCourse(UUID id, Course course);
    void deleteCourse(UUID id);
    List<CourseResponseDto> getCoursesByType(CourseType courseType);
    List<CourseResponseDto> getAllCoursesByAdminId(UUID adminId);
    List<CourseResponseDto> getAllCoursesByTeacherId(UUID teacherId);// New method
    CourseResponseDto updateCourseType(UUID courseId, CourseType courseType);

}
