package com.example.demo.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.DriverDTO;
import com.example.demo.entities.Driver;
import com.example.demo.entities.enums.DriverStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DriverRepository;
import com.example.demo.serviceimpl.DriverServiceImpl;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    // ---------- Helper Method ----------
    private Driver getDriverEntity() {
        Driver driver = new Driver();
        driver.setDriverID(1L);
        driver.setName("Sai");
        driver.setLicenseNo("LIC123");
        driver.setContactInfo("Chennai");
        driver.setMobileNumber("1234567890");
        driver.setStatus(DriverStatus.AVAILABLE);
        return driver;
    }

    // ---------- INSERT ----------
    @Test
    void insert_ShouldSaveDriverSuccessfully() {

        DriverDTO dto = new DriverDTO();
        dto.setName("Sai");
        dto.setLicenseNo("LIC123");
        dto.setStatus(DriverStatus.AVAILABLE);

        when(driverRepository.save(any(Driver.class)))
                .thenReturn(getDriverEntity());

        DriverDTO saved = driverService.insert(dto);

        assertNotNull(saved);
        assertEquals("Sai", saved.getName());
        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void insert_ShouldThrowBadRequestException_WhenNameIsNull() {

        DriverDTO dto = new DriverDTO();

        assertThrows(BadRequestException.class,
                () -> driverService.insert(dto));
    }

    // ---------- FETCH BY ID ----------
    @Test
    void fetchByID_ShouldReturnDriver_WhenExists() {

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(getDriverEntity()));

        DriverDTO dto = driverService.fetchByID(1L);

        assertEquals(1L, dto.getDriverID());
        assertEquals("Sai", dto.getName());
    }

    @Test
    void fetchByID_ShouldThrowException_WhenNotFound() {

        when(driverRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.fetchByID(1L));
    }

    // ---------- FETCH BY STATUS ----------
    @Test
    void fetchByStatus_ShouldReturnDrivers() {

        when(driverRepository.findByStatus(DriverStatus.AVAILABLE))
                .thenReturn(Arrays.asList(getDriverEntity()));

        List<DriverDTO> list =
                driverService.fetchByStatus(DriverStatus.AVAILABLE);

        assertEquals(1, list.size());
        assertEquals(DriverStatus.AVAILABLE, list.get(0).getStatus());
    }

    // ---------- FETCH ALL ----------
    @Test
    void fetchAll_ShouldReturnAllDrivers() {

        when(driverRepository.findAll())
                .thenReturn(Arrays.asList(getDriverEntity()));

        List<DriverDTO> list = driverService.fetchAll();

        assertEquals(1, list.size());
        verify(driverRepository).findAll();
    }

    // ---------- UPDATE ----------
    @Test
    void updateDriver_ShouldUpdateSuccessfully() {

        Driver existing = getDriverEntity();

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        when(driverRepository.save(any(Driver.class)))
                .thenReturn(existing);

        DriverDTO updateDTO = new DriverDTO();
        updateDTO.setMobileNumber("9999999999");
        updateDTO.setStatus(DriverStatus.ON_ROUTE);

        DriverDTO updated =
                driverService.updateDriver(1L, updateDTO);

        assertEquals("9999999999", updated.getMobileNumber());
        assertEquals(DriverStatus.ON_ROUTE, updated.getStatus());
    }

    @Test
    void updateDriver_ShouldThrowException_WhenNotFound() {

        when(driverRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.updateDriver(1L, new DriverDTO()));
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldRemoveDriverSuccessfully() {

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(getDriverEntity()));

        driverService.delete(1L);

        verify(driverRepository).delete(any(Driver.class));
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {

        when(driverRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> driverService.delete(1L));
    }
}