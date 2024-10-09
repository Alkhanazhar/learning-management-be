package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.ebook.EbookUploadDto;
import com.affy.learningManagementSystem.models.Ebook;
import com.affy.learningManagementSystem.models.Videos;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EbookService {

    public Ebook uploadEbook(EbookUploadDto dto) throws IOException;
    public Resource loadEbookAsResource(String filename);
    public Ebook getEbookById(UUID id);
    public List<Ebook> getAllEbooks();
    public boolean deleteEbook(UUID id);
    public Optional<Ebook> toggleEbookStatus(UUID id);
    public List<Ebook> getEbooksByCourseId(UUID courseId);
    public Resource loadEbookAsResourceByPath(String filePath);
     

}
