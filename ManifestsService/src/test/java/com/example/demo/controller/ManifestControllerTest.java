package com.example.demo.controller;


import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ManifestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManifestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ManifestService manifestService;

    @InjectMocks
    private ManifestController manifestController;

    private ObjectMapper objectMapper;
    private ManifestDTO manifestDTO;
    private ManifestRequiredResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(manifestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        manifestDTO = new ManifestDTO();
        manifestDTO.setManifestID(1L);
        manifestDTO.setLoadID(100L);
        manifestDTO.setWarehouseID(200L);
        manifestDTO.setItemsJSON("{\"item\":\"Box\"}");
        manifestDTO.setCreatedBy("Admin");
        manifestDTO.setCreatedAt(LocalDateTime.now());
        manifestDTO.setManifestURI("s3://manifest.pdf");

        responseDTO = new ManifestRequiredResponseDTO();
        responseDTO.setManifest(manifestDTO);
    }

    // ────────────────── CREATE ──────────────────

    @Test
    void createManifest_ShouldReturn201_WhenCreated() throws Exception {

        when(manifestService.create(any(ManifestDTO.class)))
                .thenReturn(manifestDTO);

        mockMvc.perform(post("/cargoRoute/manifests/createManifest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manifestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Manifest created successfully."));
    }

    // ────────────────── GET BY ID ──────────────────

    @Test
    void getManifestById_ShouldReturn200_WhenFound() throws Exception {

        when(manifestService.getById(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/cargoRoute/manifests/getByManifestId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manifest.manifestID").value(1))
                .andExpect(jsonPath("$.manifest.loadID").value(100));
    }

    @Test
    void getManifestById_ShouldReturn404_WhenNotFound() throws Exception {

        when(manifestService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Manifest not found"));

        mockMvc.perform(get("/cargoRoute/manifests/getByManifestId/99"))
                .andExpect(status().isNotFound());
    }

    // ────────────────── GET ALL ──────────────────

    @Test
    void getAllManifests_ShouldReturn200_WithList() throws Exception {

        when(manifestService.getAll())
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/cargoRoute/manifests/getAllManifest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllManifests_ShouldReturn200_WithEmptyList() throws Exception {

        when(manifestService.getAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/cargoRoute/manifests/getAllManifest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ────────────────── GET BY LOAD ID ──────────────────

    @Test
    void getByLoadId_ShouldReturn200_WhenFound() throws Exception {

        when(manifestService.getByLoadID(100L))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/cargoRoute/manifests/getByLoadId/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manifest.loadID").value(100));
    }

    // ────────────────── GET BY WAREHOUSE ID ──────────────────

    @Test
    void getByWarehouseId_ShouldReturn200_WithList() throws Exception {

        when(manifestService.getByWarehouseID(200L))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/cargoRoute/manifests/getByWarehouseId/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ────────────────── UPDATE ──────────────────

    @Test
    void updateManifest_ShouldReturn200_WhenUpdated() throws Exception {

        when(manifestService.update(eq(1L), any(ManifestDTO.class)))
                .thenReturn(manifestDTO);

        mockMvc.perform(put("/cargoRoute/manifests/updateManifest/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manifestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Manifest updated successfully."));
    }

    // ────────────────── DELETE ──────────────────

    @Test
    void deleteManifest_ShouldReturn200_WhenDeleted() throws Exception {

        mockMvc.perform(delete("/cargoRoute/manifests/deleteByManifestId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Manifest deleted successfully."));
    }
}