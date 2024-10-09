package com.affy.learningManagementSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.affy.learningManagementSystem.dtos.ebook.EbookUploadDto;
import com.affy.learningManagementSystem.models.Ebook;
import com.affy.learningManagementSystem.models.Videos;
import com.affy.learningManagementSystem.services.implementation.EbookServiceImplementation;



@RestController
@RequestMapping("/api/ebooks")
public class EbookController {

    @Autowired
    private EbookServiceImplementation ebookService;

    @PostMapping("/upload")
    public ResponseEntity<Ebook> uploadEbook(@ModelAttribute EbookUploadDto ebookUploadDto) {
        try {
            Ebook uploadedEbook = ebookService.uploadEbook(ebookUploadDto);
            return ResponseEntity.ok(uploadedEbook);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(null); 
        }
    }

    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewEbook(@PathVariable String filename) {
        try {
            Resource resource = ebookService.loadEbookAsResource(filename);
            if (resource == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  
            }

            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_PDF)  
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                                 .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadEbook(@PathVariable String filename) {
        try {
            Resource resource = ebookService.loadEbookAsResource(filename);
            if (resource == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  
            }

            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                                 .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEbookById(@PathVariable UUID id) {
        try {
            Ebook ebook = ebookService.getEbookById(id);
            if (ebook == null) { 
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ebook not found with id: " + id);  
            }
            return ResponseEntity.ok(ebook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Ebook>> getAllEbooks() {
        try {
            List<Ebook> ebooks = ebookService.getAllEbooks();
            return ResponseEntity.ok(ebooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  
        }
    }

    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteEbook(@PathVariable UUID id) {
    try {
        boolean deleted = ebookService.deleteEbook(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Ebook not found with id: " + id, "status", 404));
        }
        return ResponseEntity.ok(Map.of("message", "Ebook successfully deleted", "status", 200));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An error occurred while deleting the ebook", "status", 500));
    }
}

@PutMapping("/toggle-status/{id}")
public ResponseEntity<?> toggleEbookStatus(@PathVariable UUID id) {
    Optional<Ebook> updatedEbook = ebookService.toggleEbookStatus(id);
    
    if (updatedEbook.isPresent()) {
        return ResponseEntity.ok(Map.of(
            "message", "Ebook status successfully toggled",
            "status", 200,
            "ebook", updatedEbook.get()
        ));
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "message", "Ebook not found with id: " + id,
            "status", 404
        ));
    }
}

@GetMapping("/course/{courseId}")
public ResponseEntity<?> getEbooksByCourseId(@PathVariable UUID courseId) {
    List<Ebook> ebooks = ebookService.getEbooksByCourseId(courseId);
    
    if (ebooks.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "message", "No ebooks found for course with id: " + courseId,
            "status", 404
        ));
    }
    return ResponseEntity.ok(Map.of(
        "message", "Ebooks successfully retrieved",
        "status", 200,
        "ebooks", ebooks
    ));
}

@GetMapping("/read/{id}")
public ResponseEntity<Resource> readEbookPdfById(@PathVariable UUID id) {
    try {
        // Fetch the Ebook from the database
        Ebook ebook = ebookService.getEbookById(id);
        
        if (ebook == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(null);
        }

        // Load the file as a resource
        Resource resource = ebookService.loadEbookAsResourceByPath(ebook.getFilePath());

        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(null);
        }

        // Return the PDF file as a response
        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_PDF)
                             .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + ebook.getTitle() + ".pdf\"")
                             .body(resource);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(null);
    }
}

}

 