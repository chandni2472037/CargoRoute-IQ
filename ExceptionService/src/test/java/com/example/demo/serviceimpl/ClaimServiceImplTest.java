package com.example.demo.serviceimpl;

import com.example.demo.dto.ClaimDTO;
import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ClaimStatus;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ClaimRepository;
import com.example.demo.repository.ExceptionRepository;
import com.example.demo.service.ExceptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository repo;

    @Mock
    private ExceptionRepository exceptionRepo;

    @Mock
    private ExceptionService exceptionService;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private Claim claim;
    private ClaimDTO dto;
    private ExceptionRecord exceptionRecord;
    private RequiredResponseDTO requiredResponseDTO;

    @BeforeEach
    void setUp() {
        exceptionRecord = new ExceptionRecord();
        exceptionRecord.setExceptionID(1L);
        exceptionRecord.setStatus(ExceptionStatus.PENDING);

        claim = new Claim();
        claim.setClaimID(1L);
        claim.setFiledBy("Jane Doe");
        claim.setAmountClaimed(5000.0);
        claim.setStatus(ClaimStatus.OPEN);
        claim.setExceptionRecord(exceptionRecord);

        dto = new ClaimDTO();
        dto.setClaimID(1L);
        dto.setFiledBy("Jane Doe");
        dto.setAmountClaimed(5000.0);
        dto.setStatus(ClaimStatus.OPEN);
        dto.setExceptionID(1L);

        ExceptionRecordDTO exDto = new ExceptionRecordDTO();
        exDto.setExceptionID(1L);
        requiredResponseDTO = new RequiredResponseDTO();
        requiredResponseDTO.setExceptiondto(exDto);
    }

    // ── createClaim ───────────────────────────────────────────────────────────

    @Test
    void createClaim_ShouldSaveAndReturnDTO() {
        when(exceptionRepo.findById(1L)).thenReturn(Optional.of(exceptionRecord));
        when(repo.save(any(Claim.class))).thenReturn(claim);
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        ClaimDTO result = claimService.createClaim(dto);

        assertNotNull(result);
        assertEquals(1L, result.getClaimID());
        assertEquals("Jane Doe", result.getFiledBy());
        verify(repo, times(1)).save(any(Claim.class));
    }

    @Test
    void createClaim_ShouldThrowBadRequest_WhenExceptionIdIsNull() {
        dto.setExceptionID(null);

        assertThrows(BadRequestException.class, () -> claimService.createClaim(dto));
        verify(repo, never()).save(any());
    }

    // ── getAllClaims ──────────────────────────────────────────────────────────

    @Test
    void getAllClaims_ShouldReturnList() {
        when(repo.findAll()).thenReturn(List.of(claim));
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        List<ClaimDTO> result = claimService.getAllClaims();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getClaimID());
    }

    @Test
    void getAllClaims_ShouldReturnEmptyList() {
        when(repo.findAll()).thenReturn(List.of());

        List<ClaimDTO> result = claimService.getAllClaims();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── getClaimById ──────────────────────────────────────────────────────────

    @Test
    void getClaimById_ShouldReturnDTO() {
        when(repo.findById(1L)).thenReturn(Optional.of(claim));
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        ClaimDTO result = claimService.getClaimById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getClaimID());
        assertEquals(ClaimStatus.OPEN, result.getStatus());
    }

    @Test
    void getClaimById_ShouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> claimService.getClaimById(99L));
    }

    // ── updateClaimStatus ─────────────────────────────────────────────────────

    @Test
    void updateClaimStatus_ShouldUpdateAndReturn() {
        Claim settled = new Claim();
        settled.setClaimID(1L);
        settled.setStatus(ClaimStatus.SETTLED);
        settled.setExceptionRecord(exceptionRecord);

        when(repo.findById(1L)).thenReturn(Optional.of(claim));
        when(repo.save(any(Claim.class))).thenReturn(settled);
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        ClaimDTO result = claimService.updateClaimStatus(1L, ClaimStatus.SETTLED);

        assertNotNull(result);
        assertEquals(ClaimStatus.SETTLED, result.getStatus());
        verify(repo).save(any(Claim.class));
    }

    @Test
    void updateClaimStatus_ShouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> claimService.updateClaimStatus(99L, ClaimStatus.SETTLED));
    }

    // ── getClaimByExceptionId ─────────────────────────────────────────────────

    @Test
    void getClaimByExceptionId_ShouldReturnList() {
        when(repo.findByExceptionRecord_ExceptionID(1L)).thenReturn(List.of(claim));
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        List<ClaimDTO> result = claimService.getClaimByExceptionId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ── getClaimByStatus ──────────────────────────────────────────────────────

    @Test
    void getClaimByStatus_ShouldReturnList() {
        when(repo.findByStatus(ClaimStatus.OPEN)).thenReturn(List.of(claim));
        when(exceptionService.getExceptionById(1L)).thenReturn(requiredResponseDTO);

        List<ClaimDTO> result = claimService.getClaimByStatus(ClaimStatus.OPEN);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClaimStatus.OPEN, result.get(0).getStatus());
    }

    @Test
    void getClaimByStatus_ShouldReturnEmptyList() {
        when(repo.findByStatus(ClaimStatus.CANCELLED)).thenReturn(List.of());

        List<ClaimDTO> result = claimService.getClaimByStatus(ClaimStatus.CANCELLED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
