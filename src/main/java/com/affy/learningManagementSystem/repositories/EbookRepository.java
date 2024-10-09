package com.affy.learningManagementSystem.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.affy.learningManagementSystem.models.Ebook;

@Repository
public interface EbookRepository extends JpaRepository<Ebook, UUID> {
    List<Ebook> findByCourseId(UUID courseId);


}
   