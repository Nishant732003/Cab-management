package com.cabbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FileUploadServiceImpl.
 *
 * Tests cover file upload and deletion functionalities:
 * - uploadFile(): Uploads a file with a unique UUID-based filename
 * - deleteFile(): Deletes an existing file or does nothing if the file does not exist
 *
 * Dependencies:
 * - Uses MockMultipartFile to simulate file input
 * - Uses Mockito to mock UUID generation for predictable filenames
 */
@ExtendWith(MockitoExtension.class)
public class FileUploadServiceImplTest {

    // Service under test with injected dependencies
    @InjectMocks
    private FileUploadServiceImpl fileUploadService;

    // Directory used for file uploads in tests
    private static final String UPLOAD_DIR = "uploads";

    // Test file name and content
    private static final String TEST_FILENAME = "testfile.jpg";
    private static final byte[] TEST_CONTENT = "Test file content".getBytes();

    // Path object representing the upload directory
    private Path mockDir;

    /**
     * Sets up the test environment before each test method.
     * Ensures the upload directory exists.
     */
    @BeforeEach
    void setUp() throws IOException {
        mockDir = Paths.get(UPLOAD_DIR);
        Files.createDirectories(mockDir);
    }

    /**
     * Tests that uploadFile() successfully uploads a file
     * and returns a unique filename based on UUID.
     *
     * Workflow:
     * - Creates a MockMultipartFile to simulate file input
     * - Mocks UUID.randomUUID() to return a predictable value
     * - Calls uploadFile() and asserts the returned filename starts with the mocked UUID
     * - Cleans up the created file after test
     */
    @Test
    void uploadFile_validFile_returnsFilename() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", TEST_FILENAME, "image/jpeg", TEST_CONTENT);

        // Mock UUID generation for predictable filename
        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            UUID mockUuid = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            String filename = fileUploadService.uploadFile(mockFile);

            assertNotNull(filename);
            assertTrue(filename.startsWith("123e4567"));

            // Clean up the created file
            Files.delete(mockDir.resolve(filename));
        }
    }

    /**
     * Tests that deleteFile() successfully deletes an existing file.
     *
     * Workflow:
     * - Creates a temporary file in the upload directory
     * - Calls deleteFile() with the filename
     * - Asserts that the file no longer exists
     */
    @Test
    void deleteFile_existingFile_deletesFile() throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + ".txt";
        Path filePath = mockDir.resolve(uniqueFilename);
        Files.write(filePath, "dummy content".getBytes());

        fileUploadService.deleteFile(uniqueFilename);

        assertFalse(Files.exists(filePath));
    }

    /**
     * Tests that deleteFile() does not throw an exception
     * when attempting to delete a file that does not exist.
     */
    @Test
    void deleteFile_nonExistingFile_noExceptionThrown() {
        assertDoesNotThrow(() -> fileUploadService.deleteFile("nonexistent.txt"));
    }
}
