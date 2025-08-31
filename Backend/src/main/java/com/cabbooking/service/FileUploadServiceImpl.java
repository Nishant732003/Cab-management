package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Saves the uploaded file to the local filesystem.
     *
     * @param file The file to save.
     * @return The unique filename generated for the stored file.
     * @throws IOException If there's an error saving the file.
     */
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Create the upload directory if it doesn't exist
        Path directoryPath = Paths.get(uploadPath);
        Files.createDirectories(directoryPath);

        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path filePath = directoryPath.resolve(fileName);

        // Save the file to the target path
        Files.copy(file.getInputStream(), filePath);

        // Return the unique filename, not the full path
        return fileName;
    }

    /**
     * Deletes a file from the local upload directory.
     *
     * @param fileName The name of the file to delete.
     * @throws IOException If there's an error deleting the file.
     */
    @Override
    public void deleteFile(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return; // Do nothing if there's no file to delete
        }

        Path filePath = Paths.get(uploadPath).resolve(fileName);

        // Delete the file only if it actually exists
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
