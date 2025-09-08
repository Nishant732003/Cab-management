package com.cabbooking.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling file-related operations.
 * 
 * Endpoints:
 * - GET /api/files/view/{filename}: Serves a file stored on the server.
 * 
 * Main Responsibilities: 
 * - Provides a public endpoint to serve files stored locally on the server.
 * - Handles file retrieval and streaming to clients.
 * 
 * Dependencies:
 * - None
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    // Injects the upload path from your application.properties file.
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Endpoint to retrieve and serve a specific file by its filename.
     * 
     * GET /api/files/view/{filename}
     * 
     * Workflow: 
     * - Validates the filename to prevent path traversal attacks.
     * - Constructs the full file path based on the configured upload directory.
     * - Checks if the file exists and is readable.
     * - Determines the file's content type for proper HTTP response headers.
     * - Streams the file back to the client with appropriate headers.
     * - Handles errors gracefully, returning 404 if the file is not found or 400 for bad requests.
     * - Logs relevant information for debugging and monitoring purposes.
     *
     * @param filename The unique name of the file to be served.
     * @return A ResponseEntity containing the file as a resource, or a 404 Not
     * Found if the file doesn't exist.
     */
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        logger.info("Request to serve file: {}", filename);
        try {
            logger.debug("Upload path: {}", uploadPath);

            // Base directory
            Path baseDir = Paths.get(uploadPath).toAbsolutePath().normalize();

            // Resolve and normalize requested file path
            Path requested = baseDir.resolve(filename).normalize();

            // Prevent path traversal: requested must remain under baseDir
            if (!requested.startsWith(baseDir)) {
                logger.warn("Attempted path traversal attack: {}", filename);
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(requested.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.warn("File not found or not readable: {}", filename);
                return ResponseEntity.notFound().build();
            }

            // Probe content type (fallback if unknown)
            String contentType = java.nio.file.Files.probeContentType(requested);
            MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType)
                                                        : MediaType.APPLICATION_OCTET_STREAM;

            logger.info("Serving file: {} with content type: {}", filename, mediaType);

            return ResponseEntity.ok()
                    // For images, inline is default; header may be omitted
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(mediaType)
                    .body(resource);
        } catch (MalformedURLException e) {
            // Invalid file path
            logger.error("Invalid file path: {}", filename);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            // Error determining file type
            logger.error("Error determining file type for: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}