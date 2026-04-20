package com.example.demo.controller;

import com.example.demo.dto.ClaimDTO;
import com.example.demo.entity.enums.ClaimStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClaimService service;

    @InjectMocks
    private ClaimController claimController;

    private ObjectMapper objectMapper;
    private ClaimDTO claimDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(claimController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        claimDTO = new ClaimDTO();
        claimDTO.setClaimID(1L);
        claimDTO.setFiledBy("Jane Doe");
        claimDTO.setAmountClaimed(5000.0);
        claimDTO.setResolutionNotes("Awaiting review");
        claimDTO.setStatus(ClaimStatus.OPEN);
        claimDTO.setExceptionID(1L);
    }

    // ── POST /cargoRoute/claim/addClaim ───────────────────────────────────────

    @Test
    void addClaim_ShouldReturn201_WhenCreated() throws Exception {
        when(service.createClaim(any(ClaimDTO.class))).thenReturn(claimDTO);

        mockMvc.perform(post("/cargoRoute/claim/addClaim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Claim filed successfully."));
    }

    // ── GET /cargoRoute/claim/getClaims ───────────────────────────────────────

    @Test
    void fetchAllClaims_ShouldReturn200_WithList() throws Exception {
        when(service.getAllClaims()).thenReturn(List.of(claimDTO));

        mockMvc.perform(get("/cargoRoute/claim/getClaims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].claimID").value(1))
                .andExpect(jsonPath("$[0].filedBy").value("Jane Doe"));
    }

    @Test
    void fetchAllClaims_ShouldReturn200_WithEmptyList() throws Exception {
        when(service.getAllClaims()).thenReturn(List.of());

        mockMvc.perform(get("/cargoRoute/claim/getClaims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /cargoRoute/claim/getClaim/{id} ───────────────────────────────────

    @Test
    void fetchClaimById_ShouldReturn200_WhenFound() throws Exception {
        when(service.getClaimById(1L)).thenReturn(claimDTO);

        mockMvc.perform(get("/cargoRoute/claim/getClaimByID/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimID").value(1))
                .andExpect(jsonPath("$.amountClaimed").value(5000.0));
    }

    @Test
    void fetchClaimById_ShouldReturn404_WhenNotFound() throws Exception {
        when(service.getClaimById(99L))
                .thenThrow(new ResourceNotFoundException("Claim with ID 99 not found"));

        mockMvc.perform(get("/cargoRoute/claim/getClaimByID/99"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /cargoRoute/claim/updateClaimStatus/{id} ────────────────────────

    @Test
    void modifyClaimStatus_ShouldReturn200_WhenUpdated() throws Exception {
        when(service.updateClaimStatus(1L, ClaimStatus.SETTLED)).thenReturn(claimDTO);

        mockMvc.perform(patch("/cargoRoute/claim/updateClaimStatus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SETTLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Claim status updated successfully."));
    }

    @Test
    void modifyClaimStatus_ShouldReturn404_WhenNotFound() throws Exception {
        when(service.updateClaimStatus(eq(99L), any(ClaimStatus.class)))
                .thenThrow(new ResourceNotFoundException("Claim with ID 99 not found"));

        mockMvc.perform(patch("/cargoRoute/claim/updateClaimStatus/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SETTLED\"}"))
                .andExpect(status().isNotFound());
    }

    // ── GET /cargoRoute/claim/getClaimByException/{exceptionId} ──────────────

    @Test
    void fetchClaimByExceptionId_ShouldReturn200_WithList() throws Exception {
        when(service.getClaimByExceptionId(1L)).thenReturn(List.of(claimDTO));

        mockMvc.perform(get("/cargoRoute/claim/getClaimByExceptionID/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].exceptionID").value(1));
    }

    // ── GET /cargoRoute/claim/getClaimByStatus/{status} ──────────────────────

    @Test
    void fetchByClaimStatus_ShouldReturn200_WithList() throws Exception {
        when(service.getClaimByStatus(ClaimStatus.OPEN)).thenReturn(List.of(claimDTO));

        mockMvc.perform(get("/cargoRoute/claim/getClaimByStatus/OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }
}
