package com.affy.learningManagementSystem.services.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.affy.learningManagementSystem.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.affy.learningManagementSystem.dtos.video.Status;
import com.affy.learningManagementSystem.models.Videos;
import com.affy.learningManagementSystem.repositories.VideoRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Service
public class VideoServiceImplementation implements VideoService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public List<Videos> uploadVideos(List<MultipartFile> files, UUID courseId, String title, String description) throws IOException {
        List<String> videoPaths = new ArrayList<>();

        // Loop through and upload each video file to Cloudinary
        for (MultipartFile file : files) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
            String videoUrl = (String) uploadResult.get("url");
            videoPaths.add(videoUrl); 
        }

        Videos video = new Videos();
        video.setTitle(title);
        video.setDescription(description);
        video.setCourseId(courseId);
        video.setVideoPaths(videoPaths); 

        Videos savedVideo = videoRepository.save(video);

        List<Videos> uploadedVideos = new ArrayList<>();
        uploadedVideos.add(savedVideo);

        return uploadedVideos;
    }

    @Override
    public List<Videos> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public Optional<Videos> getVideoById(UUID id) {
        return videoRepository.findById(id);
    }

    @Override
    public Optional<Videos> updateVideo(UUID id, String title, String description, UUID courseId) {
        Optional<Videos> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Videos video = videoOptional.get();
            video.setTitle(title);
            video.setDescription(description);
            video.setCourseId(courseId);
            return Optional.of(videoRepository.save(video));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteVideo(UUID id) {
        if (videoRepository.existsById(id)) {
            videoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Videos> toggleVideoStatus(UUID id) {
        Optional<Videos> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Videos video = videoOptional.get();
    
            // Toggle the status
            if (video.getStatus() == Status.ACTIVE) {
                video.setStatus(Status.INACTIVE);
            } else {
                video.setStatus(Status.ACTIVE);
            }
    
            // Save the updated video
            return Optional.of(videoRepository.save(video));
        }
        return Optional.empty();
    }

    @Override
    public List<Videos> getAllActiveVideos() {
        return videoRepository.findByStatus(Status.ACTIVE);
    }

    @Override
    public List<Videos> getAllInactiveVideos() {
        return videoRepository.findByStatus(Status.INACTIVE);
    }

    @Override
    public List<Videos> getVideosByCourseId(UUID courseId) {
        return videoRepository.findByCourseId(courseId);
    }
}
