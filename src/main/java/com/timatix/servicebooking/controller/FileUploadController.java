package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload/{category}")
    public ResponseEntity<?> uploadFile(
            @PathVariable String category,
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUploadService.uploadFile(file, category);

            Map<String, Object> response = new HashMap<>();
            response.put("filePath", filePath);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error uploading file", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "File upload failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{category}/{filename}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String category,
            @PathVariable String filename) {
        try {
            String filePath = category + "/" + filename;
            fileUploadService.deleteFile(filePath);

            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "File deletion failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}