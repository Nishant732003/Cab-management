package com.cabbooking.service;

import com.cabbooking.dto.CabUpdateRequest;
import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CabServiceImplTest {

    @Mock
    private CabRepository cabRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private IFileUploadService fileUploadService;

    @InjectMocks
    private CabServiceImpl cabService;

    private Cab testCab;
    private Driver testDriver;
    private CabUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testCab = new Cab();
        testCab.setCabId(1);
        testCab.setCarType("Sedan");
        testCab.setNumberPlate("DL1ABC1234");
        testCab.setPerKmRate(15.0f);
        testCab.setIsAvailable(true);

        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setCab(testCab);
        testDriver.setVerified(true);
        testDriver.setIsAvailable(true);
        testDriver.setLatitude(28.7041);
        testDriver.setLongitude(77.1025);

        testCab.setDriver(testDriver);

        updateRequest = new CabUpdateRequest();
        updateRequest.setCarType("SUV");
        updateRequest.setNumberPlate("UP1DEF5678");
        updateRequest.setPerKmRate(20.0f);
    }

    @Test
    void updateCabDetails_validRequest_updatesAndReturnsCab() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(testDriver));
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        Cab updatedCab = cabService.updateCabDetails(1, updateRequest);

        assertNotNull(updatedCab);
        assertEquals("SUV", updatedCab.getCarType());
        assertEquals("UP1DEF5678", updatedCab.getNumberPlate());
        assertEquals(20.0f, updatedCab.getPerKmRate());
        verify(driverRepository, times(1)).findById(1);
        verify(cabRepository, times(1)).save(testCab);
    }

    @Test
    void updateCabDetails_driverNotFound_throwsException() {
        when(driverRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cabService.updateCabDetails(99, updateRequest));
        verify(driverRepository, times(1)).findById(99);
        verify(cabRepository, never()).save(any(Cab.class));
    }

    @Test
    void getAllFareEstimates_multipleNearbyDrivers_returnsCorrectEstimates() {
        // Mock a second driver for a different cab type
        Cab suvCab = new Cab();
        suvCab.setCarType("SUV");
        suvCab.setPerKmRate(25.0f);
        suvCab.setIsAvailable(true);
        Driver suvDriver = new Driver();
        suvDriver.setCab(suvCab);
        suvDriver.setVerified(true);
        suvDriver.setIsAvailable(true);
        suvDriver.setLatitude(28.7141);
        suvDriver.setLongitude(77.1125);
        suvCab.setDriver(suvDriver);

        when(driverRepository.findAll()).thenReturn(Arrays.asList(testDriver, suvDriver));

        List<FareEstimateResponse> estimates = cabService.getAllFareEstimates(10.0f, 28.7050, 77.1050);

        assertNotNull(estimates);
        assertEquals(2, estimates.size());
        assertEquals("Sedan", estimates.get(0).getCarType());
        assertEquals(150.0f, estimates.get(0).getMinFare());
        assertEquals(150.0f, estimates.get(0).getMaxFare());
        assertEquals("SUV", estimates.get(1).getCarType());
        assertEquals(250.0f, estimates.get(1).getMinFare());
        assertEquals(250.0f, estimates.get(1).getMaxFare());
    }

    @Test
    void uploadImage_withExistingImage_deletesOldAndUploadsNew() throws IOException {
        testCab.setImageUrl("/api/files/old-image.jpg");
        when(cabRepository.findById(1)).thenReturn(Optional.of(testCab));
        when(fileUploadService.uploadFile(any(MockMultipartFile.class))).thenReturn("new-image.jpg");
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        MockMultipartFile newFile = new MockMultipartFile("file", "new-image.jpg", "image/jpeg", "new data".getBytes());
        Cab result = cabService.uploadImage(1, newFile);

        assertNotNull(result);
        assertEquals("/api/files/new-image.jpg", result.getImageUrl());
        verify(fileUploadService, times(1)).deleteFile("old-image.jpg");
        verify(fileUploadService, times(1)).uploadFile(newFile);
        verify(cabRepository, times(1)).save(testCab);
    }

    @Test
    void removeImage_withExistingImage_deletesFileAndClearsUrl() throws IOException {
        testCab.setImageUrl("/api/files/existing-image.jpg");
        when(cabRepository.findById(1)).thenReturn(Optional.of(testCab));
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        Cab result = cabService.removeImage(1);

        assertNotNull(result);
        assertNull(result.getImageUrl());
        verify(fileUploadService, times(1)).deleteFile("existing-image.jpg");
        verify(cabRepository, times(1)).save(testCab);
    }
}