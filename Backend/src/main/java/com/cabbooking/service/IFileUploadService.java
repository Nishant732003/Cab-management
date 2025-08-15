package com.cabbooking.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {

    /**
     * Uploads a file to the local filesystem.
     *
     * @param file The file to upload.
     * @return The unique filename generated for the stored file.
     * @throws IOException If there's an error saving the file.
     */
    public String uploadFile(MultipartFile file) throws IOException;

    /**
     * Deletes a file from the local upload directory.
     *
     * @param fileName The name of the file to delete.
     * @throws IOException If there's an error deleting the file.
     */
    public void deleteFile(String fileName) throws IOException;
}
