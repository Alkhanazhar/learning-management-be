package com.affy.learningManagementSystem.services.implementation;

import java.io.IOException;

import com.affy.learningManagementSystem.services.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.affy.learningManagementSystem.dtos.ebook.EbookUploadDto;
import com.affy.learningManagementSystem.dtos.video.Status;
import com.affy.learningManagementSystem.models.Ebook;
import com.affy.learningManagementSystem.models.Videos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import com.affy.learningManagementSystem.repositories.EbookRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class EbookServiceImplementation implements EbookService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private EbookRepository ebookRepository;


    @Override
    public Ebook uploadEbook(EbookUploadDto dto) throws IOException {
        MultipartFile ebookFile = dto.getEbookFile();
        String filename = ebookFile.getOriginalFilename();
        String filePath = Paths.get(uploadDir, filename).toString();

        File file = new File(uploadDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        Files.copy(ebookFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        Ebook ebook = new Ebook();
        ebook.setTitle(dto.getTitle());
        ebook.setAuthor(dto.getAuthor());
        ebook.setCourseId(dto.getCourseId());
        ebook.setCategory(dto.getCategory());

        // Set full URL path to the file
        String fileUrl = baseUrl + "/api/ebooks/view/" + filename;
        ebook.setFilePath(fileUrl);

        return ebookRepository.save(ebook);
    }

    @Override
    public Resource loadEbookAsResource(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + filename, e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filename, e); 
        }
    }

    @Override
    public Ebook getEbookById(UUID id) {
        return ebookRepository.findById(id).orElse(null);
    }

    @Override
    public List<Ebook> getAllEbooks() {
        return ebookRepository.findAll();
    }

    @Override
    public boolean deleteEbook(UUID id) {
        if (ebookRepository.existsById(id)) {
            ebookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Ebook> toggleEbookStatus(UUID id) {
        Optional<Ebook> ebookOptional = ebookRepository.findById(id);
        if (ebookOptional.isPresent()) {
            Ebook ebook = ebookOptional.get();
    
            // Toggle the status
            if (ebook.getStatus() == Status.ACTIVE) {
                ebook.setStatus(Status.INACTIVE);
            } else {
                ebook.setStatus(Status.ACTIVE);
            }
    
            // Save the updated ebook
            return Optional.of(ebookRepository.save(ebook));
        }
        return Optional.empty();
    }

    @Override
     public List<Ebook> getEbooksByCourseId(UUID courseId) {
        return ebookRepository.findByCourseId(courseId);
    }
    
    @Override
    public Resource loadEbookAsResourceByPath(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
    
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found at path: " + filePath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file from path: " + filePath, e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found at path: " + filePath, e);
        }
    }

}
