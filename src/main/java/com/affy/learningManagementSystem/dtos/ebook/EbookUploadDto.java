package com.affy.learningManagementSystem.dtos.ebook;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

import java.util.UUID;

@Data
public class EbookUploadDto {
    private String title;
    private String author;
    private String category;
    private UUID courseId;
    private MultipartFile ebookFile;

   
}
