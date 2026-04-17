package com.example.demo.service;


import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.LoadResponseDTO;
import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.dto.VehicleDTO;

import com.example.demo.entities.Manifest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ManifestRepository;
import com.example.demo.serviceimpl.ManifestServiceImpl;
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
class ManifestServiceImplTest {

    @Mock
    private ManifestRepository manifestRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ManifestServiceImpl manifestService;

    private Manifest manifest;
    private ManifestDTO manifestDTO;

    @BeforeEach
    void setUp() {

        manifest = new Manifest();
        manifest.setManifestID(1L);
        manifest.setLoadID(100L);
        manifest.setWarehouseID(200L);
        manifest.setItemsJSON("{\"item\":\"Box\"}");
        manifest.setCreatedBy("Admin");
        manifest.setCreatedAt(LocalDateTime.now());
        manifest.setManifestURI("s3://manifest.pdf");

        manifestDTO = new ManifestDTO();
        manifestDTO.setLoadID(100L);
        manifestDTO.setWarehouseID(200L);
        manifestDTO.setItemsJSON("{\"item\":\"Box\"}");
        manifestDTO.setCreatedBy("Admin");
        manifestDTO.setCreatedAt(LocalDateTime.now());
        manifestDTO.setManifestURI("s3://manifest.pdf");
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_ShouldSaveAndReturnDTO() {

        when(manifestRepository.save(any(Manifest.class)))
                .thenReturn(manifest);

        ManifestDTO result = manifestService.create(manifestDTO);

        assertNotNull(result);
        assertEquals(100L, result.getLoadID());
        assertEquals(200L, result.getWarehouseID());
        assertEquals("Admin", result.getCreatedBy());
        verify(manifestRepository, times(1)).save(any(Manifest.class));
    }

    // ── getById ──────────────────────────────────────────────────────

    @Test
    void getById_ShouldReturnManifest_WhenFound() {

        LoadDTO loadDTO = new LoadDTO();
        loadDTO.setVehicleID(300L);

        LoadResponseDTO loadResponse = new LoadResponseDTO();
        loadResponse.setLoad(loadDTO);

        when(manifestRepository.findById(1L))
                .thenReturn(Optional.of(manifest));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(loadResponse);
        when(restTemplate.getForObject(anyString(), eq(VehicleDTO.class)))
                .thenReturn(new VehicleDTO());

        ManifestRequiredResponseDTO result =
                manifestService.getById(1L);

        assertNotNull(result);
        assertNotNull(result.getManifest());
        assertNotNull(result.getLoad());
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {

        when(manifestRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> manifestService.getById(99L));
    }

    // ── getAll ───────────────────────────────────────────────────────

    @Test
    void getAll_ShouldReturnAllManifests() {

        when(manifestRepository.findAll())
                .thenReturn(List.of(manifest));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        List<ManifestRequiredResponseDTO> result =
                manifestService.getAll();

        assertEquals(1, result.size());
        verify(manifestRepository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoManifests() {

        when(manifestRepository.findAll())
                .thenReturn(List.of());

        List<ManifestRequiredResponseDTO> result =
                manifestService.getAll();

        assertTrue(result.isEmpty());
    }

    // ── getByLoadID ──────────────────────────────────────────────────

    @Test
    void getByLoadID_ShouldReturnManifest() {

        when(manifestRepository.findByLoadID(100L))
                .thenReturn(manifest);
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        ManifestRequiredResponseDTO result =
                manifestService.getByLoadID(100L);

        assertEquals(100L, result.getManifest().getLoadID());
    }

    @Test
    void getByLoadID_ShouldThrowException_WhenNotFound() {

        when(manifestRepository.findByLoadID(100L))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> manifestService.getByLoadID(100L));
    }

    // ── getByWarehouseID ─────────────────────────────────────────────

    @Test
    void getByWarehouseID_ShouldReturnList() {

        when(manifestRepository.findByWarehouseID(200L))
                .thenReturn(List.of(manifest));
        when(restTemplate.getForObject(anyString(), eq(LoadResponseDTO.class)))
                .thenReturn(null);

        List<ManifestRequiredResponseDTO> result =
                manifestService.getByWarehouseID(200L);

        assertEquals(1, result.size());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_ShouldModifyAndReturnDTO() {

        when(manifestRepository.findById(1L))
                .thenReturn(Optional.of(manifest));
        when(manifestRepository.save(any(Manifest.class)))
                .thenReturn(manifest);

        ManifestDTO updateDTO = new ManifestDTO();
        updateDTO.setItemsJSON("{\"item\":\"Updated\"}");
        updateDTO.setManifestURI("updated-uri");

        ManifestDTO result =
                manifestService.update(1L, updateDTO);

        assertEquals("updated-uri", result.getManifestURI());
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {

        when(manifestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> manifestService.update(1L, manifestDTO));
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_ShouldDeleteManifest() {

        when(manifestRepository.existsById(1L))
                .thenReturn(true);

        assertDoesNotThrow(() -> manifestService.delete(1L));
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {

        when(manifestRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> manifestService.delete(1L));
    }
}