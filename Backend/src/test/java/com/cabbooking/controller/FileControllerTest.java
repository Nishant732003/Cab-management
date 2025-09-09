package com.cabbooking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class FileControllerTest {

    @InjectMocks
    private FileController fileController;

    private static final String UPLOAD_PATH = "test-uploads";

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fileController, "uploadPath", UPLOAD_PATH);
    }

    @Test
    void serveFile_existingFile_returnsFile() throws Exception {
        String filename = "test.jpg";
        Path baseDir = mock(Path.class);
        Path requestedPath = mock(Path.class);
        
        when(baseDir.toAbsolutePath()).thenReturn(baseDir);
        when(baseDir.normalize()).thenReturn(baseDir);
        when(baseDir.resolve(filename)).thenReturn(requestedPath);
        when(requestedPath.normalize()).thenReturn(requestedPath);
        when(requestedPath.startsWith(baseDir)).thenReturn(true);
        when(requestedPath.toUri()).thenReturn(new URI("file:///test.jpg"));

        // Use mockito-inline to mock static `Paths` and `Files`
        try (var mockedPaths = mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);
            try (var mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("image/jpeg");
                mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
                mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);

                ResponseEntity<?> response = fileController.serveFile(filename);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("inline; filename=\"test.jpg\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
                assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
            }
        }
    }

    @Test
    void serveFile_fileNotFound_returnsNotFound() throws Exception {
        String filename = "nonexistent.jpg";
        Path baseDir = mock(Path.class);
        Path requestedPath = mock(Path.class);
        
        when(baseDir.toAbsolutePath()).thenReturn(baseDir);
        when(baseDir.normalize()).thenReturn(baseDir);
        when(baseDir.resolve(filename)).thenReturn(requestedPath);
        when(requestedPath.normalize()).thenReturn(requestedPath);
        when(requestedPath.startsWith(baseDir)).thenReturn(true);
        
        try (var mockedPaths = mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);
            try (var mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

                ResponseEntity<?> response = fileController.serveFile(filename);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            }
        }
    }

    @Test
    void serveFile_pathTraversalAttack_returnsBadRequest() throws Exception {
        String filename = "../etc/passwd";
        Path baseDir = mock(Path.class);
        Path requestedPath = mock(Path.class);
        
        when(baseDir.toAbsolutePath()).thenReturn(baseDir);
        when(baseDir.normalize()).thenReturn(baseDir);
        when(baseDir.resolve(filename)).thenReturn(requestedPath);
        when(requestedPath.normalize()).thenReturn(requestedPath);
        when(requestedPath.startsWith(baseDir)).thenReturn(false);

        try (var mockedPaths = mockStatic(Paths.class)) {
            mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);
            
            ResponseEntity<?> response = fileController.serveFile(filename);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
}