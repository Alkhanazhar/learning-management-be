package com.affy.learningManagementSystem.services.implementation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.affy.learningManagementSystem.dtos.course.CourseAddDto;
import com.affy.learningManagementSystem.dtos.course.CourseResponseDto;
import com.affy.learningManagementSystem.models.Teacher;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.AdminRepository;
import com.affy.learningManagementSystem.repositories.TeacherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.affy.learningManagementSystem.dtos.course.CourseType;
import com.affy.learningManagementSystem.models.Course;
import com.affy.learningManagementSystem.repositories.CourseRepository;
import com.affy.learningManagementSystem.services.CourseService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CourseServiceImplementation implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ModelMapper modelMapper;



    @Override
    public CourseResponseDto createCourse(CourseAddDto courseAddDto) throws IOException {
        Teacher teacher = teacherRepository.findById(courseAddDto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        User admin = adminRepository.findById(courseAddDto.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        // Upload thumbnail to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(courseAddDto.getThumbnail().getBytes(), ObjectUtils.asMap("resource_type", "image"));
        // Extract the URL of the uploaded image
        String thumbnailUrl = (String) uploadResult.get("secure_url");

        // Create a new course entity using data from the DTO
        Course course = new Course(
                courseAddDto.getCourseName(),
                courseAddDto.getTitle(),
                courseAddDto.getDescription(),
                thumbnailUrl, // Set the uploaded thumbnail URL
                courseAddDto.getCourseType(), // Set courseType directly
                teacher,
                admin
        );

        // Save the course to the database
//        return courseRepository.save(course);
        Course savedCourse = courseRepository.save(course);
        return modelMapper.map(savedCourse, CourseResponseDto.class);
    }

    @Override
    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDto getCourseById(UUID id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(value -> modelMapper.map(value, CourseResponseDto.class)).orElse(null);
    }

    @Override
    public CourseResponseDto updateCourse(UUID id, Course courseDetails) {
        Optional<Course> existingCourse = courseRepository.findById(id);
        if (existingCourse.isPresent()) {
            Course course = existingCourse.get();
            course.setCourseName(courseDetails.getCourseName());
            course.setTitle(courseDetails.getTitle());
            course.setDescription(courseDetails.getDescription());
            course.setThumbnail(courseDetails.getThumbnail());
            Course updatedCourse = courseRepository.save(course);
            return modelMapper.map(updatedCourse, CourseResponseDto.class);
        } else {
            return null;
        }
    }

    @Override
    public void deleteCourse(UUID id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Course with id " + id + " not found");
        }
    }

    @Override
    public List<CourseResponseDto> getCoursesByType(CourseType courseType) {
        List<Course> courses = courseRepository.findByCourseType(courseType);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesByAdminId(UUID adminId) {
        List<Course> courses = courseRepository.findAllByAdminId(adminId);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDto> getAllCoursesByTeacherId(UUID teacherId) {
        List<Course> courses = courseRepository.findAllByTeacherId(teacherId);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDto updateCourseType(UUID courseId, CourseType courseType) {
        // Find the course by its ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        // Update only the courseType
        course.setCourseType(courseType);

        // Save the updated course
        Course updatedCourse = courseRepository.save(course);

        // Return the updated course as a DTO
        return modelMapper.map(updatedCourse, CourseResponseDto.class);
    }


}

