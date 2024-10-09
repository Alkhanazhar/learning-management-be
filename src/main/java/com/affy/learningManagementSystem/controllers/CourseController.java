package com.affy.learningManagementSystem.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import com.affy.learningManagementSystem.dtos.course.CourseAddDto;
import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.affy.learningManagementSystem.dtos.course.CourseType;
import com.affy.learningManagementSystem.models.Course;
import com.affy.learningManagementSystem.services.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Create Course
    @Operation(summary = "create Course")
    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@ModelAttribute CourseAddDto courseAddDto) {
        try {
            CourseResponseDto newCourse = courseService.createCourse(courseAddDto);
            return ResponseEntity.ok(newCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Get All Courses")
    @GetMapping("/list")
    public ResponseEntity<?> getAllCourses() {
        try {
            List<CourseResponseDto> courses = courseService.getAllCourses();
    
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No courses found.");
            }
    
            return ResponseEntity.ok(courses);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error while retrieving courses: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    

    // Get Course by ID
    @Operation(summary = "Get Courses by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable UUID id) {
        try {
            CourseResponseDto course = courseService.getCourseById(id);
            if (course != null) {
                return ResponseEntity.ok(course);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Course not found with id: " + id);
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error while retrieving the course: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Update Course
    @Operation(summary = "Update Existing Course")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable UUID id, @RequestBody Course courseDetails) {
        try {
            CourseResponseDto updatedCourse = courseService.updateCourse(id, courseDetails);
            if (updatedCourse != null) {
                return ResponseEntity.ok(updatedCourse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Course not found with id: " + id);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation error: " + e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error while updating the course: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete Existing Course")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable UUID id) {
        try {
            CourseResponseDto course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found with id: " + id);
            }
    
            courseService.deleteCourse(id);
    
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred while deleting the course: " + e.getMessage());
        }
    }


    @Operation(summary = "Get All Courses By Type eg: PUBLIC")
    @GetMapping("/type/{courseType}")
    public ResponseEntity<?> getCoursesByType(@PathVariable String courseType) {
        try {
            CourseType type = CourseType.valueOf(courseType.toUpperCase());
            List<CourseResponseDto> courses = courseService.getCoursesByType(type);
    
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No courses found for type: " + type);
            }
    
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid course type: " + courseType);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error while retrieving courses by type: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Get All Courses By AdminId")
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<?> getAllCoursesByAdminId(@PathVariable UUID adminId) {
        try {
            List<CourseResponseDto> courses = courseService.getAllCoursesByAdminId(adminId);
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No courses found for admin with id: " + adminId);
            }
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Get All Courses By TeacherId")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getAllCoursesByTeacherId(@PathVariable UUID teacherId) {
        try {
            List<CourseResponseDto> courses = courseService.getAllCoursesByTeacherId(teacherId);
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No courses found for teacher with id: " + teacherId);
            }
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Update Course Type eg: PUBLIC to PRIVATE")
    @PutMapping("/updateCourseType/{courseId}")
    public ResponseEntity<?> updateCourseType(
            @PathVariable UUID courseId,
            @RequestParam CourseType courseType) {
        try {
            // Update the courseType via the service
            CourseResponseDto updatedCourse = courseService.updateCourseType(courseId, courseType);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


}


