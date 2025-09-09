package com.cabbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceImplTest {

    @InjectMocks
    private FileUploadServiceImpl fileUploadService;

    private static final String UPLOAD_DIR = "uploads";
    private static final String TEST_FILENAME = "testfile.jpg";
    private static final byte[] TEST_CONTENT = "Test file content".getBytes();
    private Path mockDir;
    
    @BeforeEach
    void setUp() throws IOException {
        mockDir = Paths.get(UPLOAD_DIR);
        // Ensure the directory exists for the test
        Files.createDirectories(mockDir);
    }
    
    @Test
    void uploadFile_validFile_returnsFilename() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", TEST_FILENAME, "image/jpeg", TEST_CONTENT);
        
        // Use Mockito to mock the UUID generation
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
    
    @Test
    void deleteFile_existingFile_deletesFile() throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + ".txt";
        Path filePath = mockDir.resolve(uniqueFilename);
        Files.write(filePath, "dummy content".getBytes());
        
        fileUploadService.deleteFile(uniqueFilename);
        
        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_nonExistingFile_noExceptionThrown() {
        assertDoesNotThrow(() -> fileUploadService.deleteFile("nonexistent.txt"));
    }
}