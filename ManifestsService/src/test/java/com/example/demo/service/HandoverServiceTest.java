package com.example.demo.service;


import com.example.demo.dto.HandoverDTO;
import com.example.demo.dto.HandoverResponseDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.entities.Handover;
import com.example.demo.entities.Manifest;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HandoverRepository;
import com.example.demo.serviceimpl.HandoverServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandoverServiceImplTest {

    @Mock
    private HandoverRepository handoverRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HandoverServiceImpl handoverService;

    private Handover handover;
    private HandoverDTO handoverDTO;

    @BeforeEach
    void setUp() {

        Manifest manifest = new Manifest();
        manifest.setManifestID(10L);

        handover = new Handover();
        handover.setHandoverID(1L);
        handover.setManifest(manifest);
        handover.setHandedBy("Warehouse Admin");
        handover.setHandedAt(LocalDateTime.now());
        handover.setReceivedBy("Driver");
        handover.setReceivedAt(LocalDateTime.now().plusHours(2));
        handover.setNotes("Handover completed");

        handoverDTO = new HandoverDTO();
        handoverDTO.setManifestID(10L);
        handoverDTO.setHandedBy("Warehouse Admin");
        handoverDTO.setHandedAt(LocalDateTime.now());
        handoverDTO.setReceivedBy("Driver");
        handoverDTO.setReceivedAt(LocalDateTime.now().plusHours(2));
        handoverDTO.setNotes("Handover completed");
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_ShouldSaveAndReturnDTO() {

        when(handoverRepository.save(any(Handover.class)))
                .thenReturn(handover);

        HandoverDTO result =
                handoverService.create(handoverDTO);

        assertNotNull(result);
        assertEquals(10L, result.getManifestID());
        assertEquals("Warehouse Admin", result.getHandedBy());
        verify(handoverRepository, times(1))
                .save(any(Handover.class));
    }

    @Test
    void create_ShouldThrowException_WhenManifestIDMissing() {

        HandoverDTO invalid = new HandoverDTO();

        assertThrows(BadRequestException.class,
                () -> handoverService.create(invalid));
    }

    // ── getById ──────────────────────────────────────────────────────

    @Test
    void getById_ShouldReturnHandover_WhenFound() {

        when(handoverRepository.findById(1L))
                .thenReturn(Optional.of(handover));
        when(restTemplate.getForObject(any(String.class),
                eq(ManifestRequiredResponseDTO.class)))
                .thenReturn(new ManifestRequiredResponseDTO());

        HandoverResponseDTO result =
                handoverService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getHandover().getHandoverID());
        assertNotNull(result.getManifestDetails());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {

        when(handoverRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> handoverService.getById(99L));
    }

    // ── getAll ───────────────────────────────────────────────────────

    @Test
    void getAll_ShouldReturnAllHandovers() {

        when(handoverRepository.findAll())
                .thenReturn(List.of(handover));
        when(restTemplate.getForObject(any(String.class),
                eq(ManifestRequiredResponseDTO.class)))
                .thenReturn(new ManifestRequiredResponseDTO());

        List<HandoverResponseDTO> result =
                handoverService.getAll();

        assertEquals(1, result.size());
        verify(handoverRepository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoHandovers() {

        when(handoverRepository.findAll())
                .thenReturn(List.of());

        List<HandoverResponseDTO> result =
                handoverService.getAll();

        assertTrue(result.isEmpty());
    }

    // ── getByManifestID ──────────────────────────────────────────────

    @Test
    void getByManifestID_ShouldReturnHandoverList() {

        when(handoverRepository.findByManifest_ManifestID(10L))
                .thenReturn(List.of(handover));   

        when(restTemplate.getForObject(
                any(String.class),
                eq(ManifestRequiredResponseDTO.class)))
                .thenReturn(new ManifestRequiredResponseDTO());

        List<HandoverResponseDTO> result =
                handoverService.getByManifestID(10L);

        assertEquals(1, result.size());
        assertEquals(10L,
                result.get(0).getHandover().getManifestID());
    }

    @Test
    void getByManifestID_ShouldThrowException_WhenNotFound() {

        when(handoverRepository.findByManifest_ManifestID(10L))
                .thenReturn(List.of());   

        assertThrows(ResourceNotFoundException.class,
                () -> handoverService.getByManifestID(10L));
    }

    // ── getByHandedBy ────────────────────────────────────────────────

    @Test
    void getByHandedBy_ShouldReturnHandovers() {

        when(handoverRepository.findByHandedBy("Warehouse Admin"))
                .thenReturn(List.of(handover));
        when(restTemplate.getForObject(any(String.class),
                eq(ManifestRequiredResponseDTO.class)))
                .thenReturn(new ManifestRequiredResponseDTO());

        List<HandoverResponseDTO> result =
                handoverService.getByHandedBy("Warehouse Admin");

        assertEquals(1, result.size());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_ShouldModifyAndReturnDTO() {

        when(handoverRepository.findById(1L))
                .thenReturn(Optional.of(handover));
        when(handoverRepository.save(any(Handover.class)))
                .thenReturn(handover);

        HandoverDTO updateDTO = new HandoverDTO();
        updateDTO.setNotes("Updated Notes");

        HandoverDTO result =
                handoverService.update(1L, updateDTO);

        assertEquals("Updated Notes", result.getNotes());
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {

        when(handoverRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> handoverService.update(1L, handoverDTO));
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_ShouldDeleteHandover() {

        when(handoverRepository.findById(1L))
                .thenReturn(Optional.of(handover));

        assertDoesNotThrow(() ->
                handoverService.delete(1L));

        verify(handoverRepository, times(1))
                .delete(handover);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {

        when(handoverRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> handoverService.delete(99L));
    }
}
