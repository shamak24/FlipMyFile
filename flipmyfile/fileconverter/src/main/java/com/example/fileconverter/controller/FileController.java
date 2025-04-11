package com.example.fileconverter.controller;
import com.example.fileconverter.service.ImageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    @GetMapping("/")
    public String home() {
        return "index";  // Loads index.html
    }

    @GetMapping("/imgconvert")
    public String convertPage() {
        return "imageconverter";  // Loads converter.html
    }

    @Autowired
    private ImageConverter imageConverter;

    @PostMapping("/upload")
    public ResponseEntity<?> handleUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format
    ) {
        try {
            // 1. Empty check
            if (file.isEmpty()) {
                return ResponseEntity
                .badRequest()
                .body("No file uploaded.".getBytes());
            }
            // 2. Size limit (e.g., 5MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body("File size exceeds 10MB limit of size.");
            }
            // 3. MIME type check (basic filtering)
            String contentType = file.getContentType();
            if (contentType == null || 
                !(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
                return ResponseEntity
                        .badRequest()
                        .body("Only PNG or JPEG images are allowed.".getBytes());
            }
            
            // Check if it's an image conversion request
            if (format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg")) {
                ByteArrayOutputStream convertedImage = imageConverter.convertImageFormat(file.getInputStream(), format);

                String fileName = "converted." + format.toLowerCase();
                ByteArrayResource resource = new ByteArrayResource(convertedImage.toByteArray());

                return ResponseEntity.ok()
                        .contentType(format.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                        .body(resource);
            }

            return ResponseEntity.badRequest().body("Unsupported format for now.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error converting file: " + e.getMessage());
        }
    }
}