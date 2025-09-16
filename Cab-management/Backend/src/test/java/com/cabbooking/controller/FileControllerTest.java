// package com.cabbooking.controller;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.springframework.test.util.ReflectionTestUtils;

// import java.io.IOException;
// import java.net.URI;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// /**
//  * Unit tests for FileController.
//  * Validates secure file serving from uploads directory,
//  * including valid file request, file-not-found, and path traversal protection.
//  */
// @ExtendWith(SpringExtension.class)
// public class FileControllerTest {

//     @InjectMocks
//     private FileController fileController;

//     private static final String UPLOAD_PATH = "test-uploads";

//     /**
//      * Initializes the controller before each test by injecting the upload path.
//      * Uses ReflectionTestUtils to set private fields.
//      */
//     @BeforeEach
//     void setUp() throws IOException {
//         MockitoAnnotations.openMocks(this);
//         ReflectionTestUtils.setField(fileController, "uploadPath", UPLOAD_PATH);
//     }

//     /**
//      * Test: GET /files/{filename}
//      * Scenario: Request an existing file (test.jpg) inside the uploads directory.
//      * Workflow:
//      * - Mock Paths.get to return base directory path
//      * - Mock path resolution and normalization
//      * - Mock Files static methods: exists, isReadable, probeContentType
//      * - Perform controller call
//      * - Assert HTTP 200 OK with correct headers and content type
//      */
//     @Test
//     void serveFile_existingFile_returnsFile() throws Exception {
//         String filename = "test.jpg";

//         // Mock Path objects for base directory and requested file
//         Path baseDir = mock(Path.class);
//         Path requestedPath = mock(Path.class);

//         // Mock baseDir behavior
//         when(baseDir.toAbsolutePath()).thenReturn(baseDir);
//         when(baseDir.normalize()).thenReturn(baseDir);

//         // Mock requestedPath behavior
//         when(baseDir.resolve(filename)).thenReturn(requestedPath);
//         when(requestedPath.normalize()).thenReturn(requestedPath);
//         when(requestedPath.startsWith(baseDir)).thenReturn(true);
//         when(requestedPath.toUri()).thenReturn(new URI("file:///test.jpg"));

//         // Mock static methods of Paths and Files
//         try (var mockedPaths = mockStatic(Paths.class)) {
//             mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);
//             try (var mockedFiles = mockStatic(Files.class)) {
//                 mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);
//                 mockedFiles.when(() -> Files.isReadable(any(Path.class))).thenReturn(true);
//                 mockedFiles.when(() -> Files.probeContentType(any(Path.class))).thenReturn("image/jpeg");

//                 // Call the controller method
//                 ResponseEntity<?> response = fileController.serveFile(filename);

//                 // Assertions
//                 assertEquals(HttpStatus.OK, response.getStatusCode());
//                 assertEquals("inline; filename=\"test.jpg\"",
//                         response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
//                 assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
//             }
//         }
//     }

//     /**
//      * Test: GET /files/{filename}
//      * Scenario: Request a non-existing file (nonexistent.jpg).
//      * Workflow:
//      * - Mock Paths.get to return base directory
//      * - Mock path resolution and normalization
//      * - Mock Files.exists to return false
//      * - Perform controller call
//      * - Assert HTTP 404 Not Found
//      */
//     @Test
//     void serveFile_fileNotFound_returnsNotFound() throws Exception {
//         String filename = "nonexistent.jpg";

//         Path baseDir = mock(Path.class);
//         Path requestedPath = mock(Path.class);

//         // Mock path resolution
//         when(baseDir.toAbsolutePath()).thenReturn(baseDir);
//         when(baseDir.normalize()).thenReturn(baseDir);
//         when(baseDir.resolve(filename)).thenReturn(requestedPath);
//         when(requestedPath.normalize()).thenReturn(requestedPath);
//         when(requestedPath.startsWith(baseDir)).thenReturn(true);

//         try (var mockedPaths = mockStatic(Paths.class)) {
//             mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);
//             try (var mockedFiles = mockStatic(Files.class)) {
//                 mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

//                 ResponseEntity<?> response = fileController.serveFile(filename);

//                 // Expect 404 for non-existing file
//                 assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//             }
//         }
//     }

//     /**
//      * Test: GET /files/{filename}
//      * Scenario: Attempt a path traversal attack (../etc/passwd).
//      * Workflow:
//      * - Mock Paths.get to return base directory
//      * - Mock path resolution and normalization to simulate malicious path
//      * - Perform controller call
//      * - Assert HTTP 400 Bad Request
//      */
//     @Test
//     void serveFile_pathTraversalAttack_returnsBadRequest() throws Exception {
//         String filename = "../etc/passwd";

//         Path baseDir = mock(Path.class);
//         Path requestedPath = mock(Path.class);

//         // Mock path resolution to simulate traversal outside baseDir
//         when(baseDir.toAbsolutePath()).thenReturn(baseDir);
//         when(baseDir.normalize()).thenReturn(baseDir);
//         when(baseDir.resolve(filename)).thenReturn(requestedPath);
//         when(requestedPath.normalize()).thenReturn(requestedPath);
//         when(requestedPath.startsWith(baseDir)).thenReturn(false); // Traversal detected

//         try (var mockedPaths = mockStatic(Paths.class)) {
//             mockedPaths.when(() -> Paths.get(UPLOAD_PATH)).thenReturn(baseDir);

//             ResponseEntity<?> response = fileController.serveFile(filename);

//             // Expect 400 Bad Request for invalid path
//             assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//         }
//     }
// }
