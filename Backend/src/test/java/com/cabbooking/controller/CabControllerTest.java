package com.cabbooking.controller;

import com.cabbooking.dto.CabUpdateRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.service.ICabService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CabControllerTest {

    @Mock
    private ICabService cabService;

    @InjectMocks
    private CabController cabController;

    private Cab testCab;
    private CabUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testCab = new Cab();
        testCab.setCabId(1);
        testCab.setCarType("Sedan");
        testCab.setNumberPlate("DL1ABC1234");
        testCab.setPerKmRate(15.0f);

        updateRequest = new CabUpdateRequest();
        updateRequest.setCarType("Sedan");
        updateRequest.setNumberPlate("DL1ABC1234");
        updateRequest.setPerKmRate(15.0f);
    }

    @Test
    void updateCabForDriver_validRequest_returnsUpdatedCab() {
        when(cabService.updateCabDetails(any(Integer.class), any(CabUpdateRequest.class))).thenReturn(testCab);

        ResponseEntity<Cab> response = cabController.updateCabForDriver(1, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCab, response.getBody());
        verify(cabService, times(1)).updateCabDetails(1, updateRequest);
    }

    @Test
    void getCabsOfType_validType_returnsCabsList() {
        when(cabService.getCabsOfType("Sedan")).thenReturn(Collections.singletonList(testCab));

        ResponseEntity<List<Cab>> response = cabController.getCabsOfType("Sedan");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cabService, times(1)).getCabsOfType("Sedan");
    }

    @Test
    void getCabById_existingId_returnsCab() {
        when(cabService.getCabById(1)).thenReturn(Optional.of(testCab));

        ResponseEntity<Cab> response = cabController.getCabById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCab, response.getBody());
        verify(cabService, times(1)).getCabById(1);
    }

    @Test
    void getCabById_nonExistingId_returnsNotFound() {
        when(cabService.getCabById(99)).thenReturn(Optional.empty());

        ResponseEntity<Cab> response = cabController.getCabById(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(cabService, times(1)).getCabById(99);
    }

    @Test
    void getAllCabs_returnsAllCabsList() {
        when(cabService.getAllCabs()).thenReturn(Collections.singletonList(testCab));

        ResponseEntity<List<Cab>> response = cabController.getAllCabs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cabService, times(1)).getAllCabs();
    }

    @Test
    void getAllAvailableCabs_returnsAvailableCabsList() {
        testCab.setIsAvailable(true);
        when(cabService.getAllAvailableCabs()).thenReturn(Collections.singletonList(testCab));

        ResponseEntity<List<Cab>> response = cabController.getAllAvailableCabs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cabService, times(1)).getAllAvailableCabs();
    }
    
    @Test
    void uploadOrUpdateCabImage_validFile_returnsUpdatedCab() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        when(cabService.uploadImage(any(Integer.class), any(MockMultipartFile.class))).thenReturn(testCab);

        ResponseEntity<Cab> response = cabController.uploadOrUpdateCabImage(1, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCab, response.getBody());
        verify(cabService, times(1)).uploadImage(1, file);
    }

    @Test
    void uploadOrUpdateCabImage_serviceThrowsException_returnsInternalServerError() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        when(cabService.uploadImage(any(Integer.class), any(MockMultipartFile.class))).thenThrow(new IOException());

        ResponseEntity<Cab> response = cabController.uploadOrUpdateCabImage(1, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(cabService, times(1)).uploadImage(1, file);
    }

    @Test
    void removeCabImage_validId_returnsUpdatedCab() throws IOException {
        when(cabService.removeImage(any(Integer.class))).thenReturn(testCab);

        ResponseEntity<Cab> response = cabController.removeCabImage(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCab, response.getBody());
        verify(cabService, times(1)).removeImage(1);
    }

    @Test
    void removeCabImage_serviceThrowsException_returnsInternalServerError() throws IOException {
        when(cabService.removeImage(any(Integer.class))).thenThrow(new IOException());

        ResponseEntity<Cab> response = cabController.removeCabImage(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(cabService, times(1)).removeImage(1);
    }
}