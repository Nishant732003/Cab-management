package com.cabbooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * REST controller for serving static files, such as profile photos.
 * This controller provides a public endpoint to access files stored locally on the server.
 * 
 * Main Responsibilities: 
 * - Provides a public endpoint to serve files stored locally on the server. 
 * 
 * Security:
 * - This controller is accessible to authenticated users.
 */
@RestController
@RequestMapping("/api/files")
@PreAuthorize("isAuthenticated()")
public class FileController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    // Injects the upload path from your application.properties file.
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Endpoint to retrieve and serve a specific file by its filename.
     * 
     * GET /api/files/{filename}
     * 
     * Workflow: 
     * - The client requests a file using the filename field. 
     * - This method constructs the full path to the file on the server's disk. 
     * - It reads the file and streams it back in the HTTP response.
     *
     * @param filename The unique name of the file to be served.
     * @return A ResponseEntity containing the file as a resource, or a 404 Not
     * Found if the file doesn't exist.
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        logger.info("Received view-file request for filename: {}", filename);
        try {
            logger.info("Serving file: {}", filename);
            // Construct the full path to the requested file.
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // Check if the file exists and is readable.
            if (resource.exists() || resource.isReadable()) {
                // Return the file with the correct content type.
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG) // You can enhance this to dynamically determine the content type
                        .body(resource);
            } else {
                logger.error("File not found: {}", filename);
                // If the file doesn't exist, return a 404 error.
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error serving file: {}", filename, e);
            // Handle cases where the file path is invalid.
            return ResponseEntity.badRequest().build();
        }
    }
}
