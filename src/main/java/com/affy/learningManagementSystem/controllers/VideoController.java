package com.affy.learningManagementSystem.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.affy.learningManagementSystem.models.Videos;
import com.affy.learningManagementSystem.services.implementation.VideoServiceImplementation;


@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoServiceImplementation videoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideos(
            @RequestParam("files") List<MultipartFile> files, 
            @RequestParam("courseId") String courseId,  // UUID as String
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        try {
            // Convert courseId from String to UUID
            UUID courseIdUUID = UUID.fromString(courseId);
    
            List<Videos> uploadedVideos = videoService.uploadVideos(files, courseIdUUID, title, description);
            return ResponseEntity.ok(uploadedVideos);
        } catch (IllegalArgumentException e) {
            // Handle invalid UUID format
            return ResponseEntity.badRequest().body("Invalid UUID format for courseId");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload videos: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Videos>> getAllVideos() {
        List<Videos> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    // Get video by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable UUID id) {
        Optional<Videos> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            return ResponseEntity.ok(video.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
        }
    }

     @PutMapping("/{id}")
    public ResponseEntity<?> updateVideo(
            @PathVariable UUID id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("courseId") UUID courseId) {

        Optional<Videos> updatedVideo = videoService.updateVideo(id, title, description, courseId);
        if (updatedVideo.isPresent()) {
            return ResponseEntity.ok(updatedVideo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
        }
    }

     @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable UUID id) {
        boolean isDeleted = videoService.deleteVideo(id);
        if (isDeleted) {
            return ResponseEntity.ok("Video deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
        }
    }

    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleVideoStatus(@PathVariable UUID id) {
    Optional<Videos> updatedVideo = videoService.toggleVideoStatus(id);
    if (updatedVideo.isPresent()) {
        return ResponseEntity.ok(updatedVideo.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found");
    }
}

    @GetMapping("/active")
    public List<Videos> getAllActiveVideos() {
    return videoService.getAllActiveVideos();
}

@GetMapping("/inactive")
public List<Videos> getAllInactiveVideos() {
    return videoService.getAllInactiveVideos();
}

@GetMapping("/course/{courseId}")
public ResponseEntity<List<Videos>> getVideosByCourseId(@PathVariable UUID courseId) {
    List<Videos> videos = videoService.getVideosByCourseId(courseId);
    return ResponseEntity.ok(videos);
}

}
