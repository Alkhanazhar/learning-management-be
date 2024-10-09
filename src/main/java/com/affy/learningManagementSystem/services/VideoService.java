package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.models.Videos;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoService {

    public List<Videos> uploadVideos(List<MultipartFile> files, UUID courseId, String title, String description) throws IOException;
    public List<Videos> getAllVideos();
    public Optional<Videos> getVideoById(UUID id);
    public Optional<Videos> updateVideo(UUID id, String title, String description, UUID courseId);
    public boolean deleteVideo(UUID id);
    public Optional<Videos> toggleVideoStatus(UUID id);
    public List<Videos> getAllActiveVideos();
    public List<Videos> getAllInactiveVideos();
    public List<Videos> getVideosByCourseId(UUID courseId);
}
