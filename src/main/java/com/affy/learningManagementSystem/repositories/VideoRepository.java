package com.affy.learningManagementSystem.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.affy.learningManagementSystem.dtos.video.Status;
import com.affy.learningManagementSystem.models.Videos;

@Repository
public interface VideoRepository extends JpaRepository<Videos, UUID> {
    List<Videos> findByStatus(Status status);
    List<Videos> findByCourseId(UUID courseId);

}
