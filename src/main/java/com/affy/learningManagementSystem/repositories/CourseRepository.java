package com.affy.learningManagementSystem.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.affy.learningManagementSystem.dtos.course.CourseType;
import com.affy.learningManagementSystem.models.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {
        List<Course> findByCourseType(CourseType courseType);
        List<Course> findAllByAdminId(UUID adminId);
        List<Course> findAllByTeacherId(UUID teacherId);

}

