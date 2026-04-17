package com.example.demo.service;


import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.DispatchResponseDTO;
import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.LoadResponseDTO;
import com.example.demo.dto.VehicleDTO;

import com.example.demo.entities.Dispatch;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DispatchRepository;
import com.example.demo.serviceimpl.DispatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceImplTest {

    @Mock
    private DispatchRepository dispatchRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DispatchServiceImpl dispatchService;

    private Dispatch dispatch;
    private DispatchDTO dispatchDTO;

    @BeforeEach
    void setUp() {
        dispatch = new Dispatch();
        dispatch.setDispatchID(1L);
        dispatch.setLoadID(100L);
        dispatch.setAssignedDriverID(200L);
        dispatch.setAssignedBy("Admin");
        dispatch.setAssignedAt(LocalDateTime.now());
        dispatch.setStatus(DispatchStatus.ASSIGNED);

        dispatchDTO = new DispatchDTO();
        dispatchDTO.setLoadID(100L);
        dispatchDTO.setAssignedDriverID(200L);
        dispatchDTO.setAssignedBy("Admin");
        dispatchDTO.setAssignedAt(LocalDateTime.now());
        dispatchDTO.setStatus(DispatchStatus.ASSIGNED);
    }

    // ── INSERT ──────────────────────────────────────────────────────

    @Test
    void insert_ShouldSaveAndReturnDispatchDTO() {

        when(dispatchRepository.save(any(Dispatch.class)))
                .thenReturn(dispatch);

        DispatchDTO result = dispatchService.insert(dispatchDTO);

        assertNotNull(result);
        assertEquals(DispatchStatus.ASSIGNED, result.getStatus());
        verify(dispatchRepository, times(1)).save(any(Dispatch.class));
    }

    // ── FETCH BY ID ──────────────────────────────────────────────────

    @Test
    void fetchByID_ShouldReturnDispatchResponse() {

        LoadDTO loadDTO = new LoadDTO();
        loadDTO.setVehicleID(300L);

        LoadResponseDTO loadResponse = new LoadResponseDTO();
        loadResponse.setLoad(loadDTO);

        VehicleDTO vehicleDTO = new VehicleDTO();

        when(dispatchRepository.findById(1L))
                .thenReturn(Optional.of(dispatch));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(loadResponse);
        when(restTemplate.getForObject(anyString(), eq(VehicleDTO.class)))
                .thenReturn(vehicleDTO);

        DispatchResponseDTO result = dispatchService.fetchByID(1L);

        assertNotNull(result);
        assertEquals(1L, result.getDispatch().getDispatchID());
        assertNotNull(result.getLoad());
        assertNotNull(result.getVehicle());
    }

    @Test
    void fetchByID_ShouldThrowException_WhenNotFound() {

        when(dispatchRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> dispatchService.fetchByID(99L));
    }

    // ── FETCH BY ASSIGNED BY ─────────────────────────────────────────

    @Test
    void fetchByAssignedBy_ShouldReturnDispatchList() {

        when(dispatchRepository.findByAssignedBy("Admin"))
                .thenReturn(List.of(dispatch));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        List<DispatchResponseDTO> result =
                dispatchService.fetchByAssignedBy("Admin");

        assertEquals(1, result.size());
        assertEquals("Admin",
                result.get(0).getDispatch().getAssignedBy());
    }

    // ── FETCH BY STATUS ──────────────────────────────────────────────

    @Test
    void fetchByStatus_ShouldReturnMatchingDispatches() {

        when(dispatchRepository.findByStatus(DispatchStatus.ASSIGNED))
                .thenReturn(List.of(dispatch));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        List<DispatchResponseDTO> result =
                dispatchService.fetchByStatus(DispatchStatus.ASSIGNED);

        assertEquals(1, result.size());
        assertEquals(DispatchStatus.ASSIGNED,
                result.get(0).getDispatch().getStatus());
    }

    // ── FETCH ALL ────────────────────────────────────────────────────

    @Test
    void fetchAll_ShouldReturnAllDispatches() {

        when(dispatchRepository.findAll())
                .thenReturn(List.of(dispatch));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        List<DispatchResponseDTO> result =
                dispatchService.fetchAll();

        assertEquals(1, result.size());
        verify(dispatchRepository, times(1)).findAll();
    }

    // ── UPDATE ───────────────────────────────────────────────────────

    @Test
    void updateDispatch_ShouldUpdateAndReturnDTO() {

        when(dispatchRepository.findById(1L))
                .thenReturn(Optional.of(dispatch));
        when(dispatchRepository.save(any(Dispatch.class)))
                .thenReturn(dispatch);

        DispatchDTO updateDTO = new DispatchDTO();
        updateDTO.setStatus(DispatchStatus.COMPLETED);

        DispatchDTO result =
                dispatchService.updateDispatch(1L, updateDTO);

        assertEquals(DispatchStatus.COMPLETED, result.getStatus());
        verify(dispatchRepository).save(any(Dispatch.class));
    }

    @Test
    void updateDispatch_ShouldThrowException_WhenNotFound() {

        when(dispatchRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> dispatchService.updateDispatch(99L, dispatchDTO));
    }

    // ── DELETE ───────────────────────────────────────────────────────

    @Test
    void delete_ShouldRemoveDispatch() {

        when(dispatchRepository.findById(1L))
                .thenReturn(Optional.of(dispatch));

        dispatchService.delete(1L);

        verify(dispatchRepository).delete(dispatch);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {

        when(dispatchRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> dispatchService.delete(99L));
    }
}