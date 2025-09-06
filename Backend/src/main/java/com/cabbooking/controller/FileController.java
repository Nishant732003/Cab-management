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
        // Base directory
        Path baseDir = Paths.get(uploadPath).toAbsolutePath().normalize();

        // Resolve and normalize requested file path
        Path requested = baseDir.resolve(filename).normalize();

        // Prevent path traversal: requested must remain under baseDir
        if (!requested.startsWith(baseDir)) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(requested.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // Probe content type (fallback if unknown)
        String contentType = java.nio.file.Files.probeContentType(requested);
        MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType)
                                                    : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                // For images, inline is default; header may be omitted
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(mediaType)
                .body(resource);
    } catch (MalformedURLException e) {
        return ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
}