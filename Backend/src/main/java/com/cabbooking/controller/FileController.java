package com.cabbooking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * REST controller for serving static files, such as profile photos. This
 * controller provides a public endpoint to access files stored locally on the
 * server.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    // Injects the upload path from your application.properties file.
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Endpoint to retrieve and serve a specific file by its filename.
     *
     * GET /api/files/{filename}
     *
     * Workflow: - The client requests a file using the URL stored in the
     * driver's profilePhotoUrl field. - This method constructs the full path to
     * the file on the server's disk. - It reads the file and streams it back in
     * the HTTP response.
     *
     * @param filename The unique name of the file to be served.
     * @return A ResponseEntity containing the file as a resource, or a 404 Not
     * Found if the file doesn't exist.
     */
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // 1. Construct the full path to the requested file.
            Path file = Paths.get(uploadPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // 2. Check if the file exists and is readable.
            if (resource.exists() || resource.isReadable()) {
                // 3. Return the file with the correct content type.
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG) // You can enhance this to dynamically determine the content type
                        .body(resource);
            } else {
                // 4. If the file doesn't exist, return a 404 error.
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // Handle cases where the file path is invalid.
            return ResponseEntity.badRequest().build();
        }
    }
}
