package com.example.demo.serviceimpl;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ClaimDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ExceptionRecord;
import com.example.demo.entity.enums.ClaimStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ClaimRepository;
import com.example.demo.repository.ExceptionRepository;
import com.example.demo.service.ClaimService;
import com.example.demo.service.ExceptionService;

@Service // Marks this class as a Spring-managed service component
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository repo; // Injects the ClaimRepository to access database operations

    @Autowired
    private ExceptionRepository exceptionRepo;

    @Autowired
    private ExceptionService exceptionService;

    // Save a new Claim or update an existing one
    public ClaimDTO createClaim(ClaimDTO dto){
        if (dto == null) {
            throw new com.example.demo.exception.BadRequestException("Claim request body must not be null");
        }
        if (dto.getExceptionID() == null) {
            throw new com.example.demo.exception.BadRequestException("Exception ID is required");
        }
        if (dto.getFiledBy() == null || dto.getFiledBy().trim().isEmpty()) {
            throw new com.example.demo.exception.BadRequestException("Filed by field is required");
        }
        if (dto.getAmountClaimed() == null || dto.getAmountClaimed() <= 0) {
            throw new com.example.demo.exception.BadRequestException("Amount claimed must be a positive number");
        }
        if (dto.getStatus() == null) {
            dto.setStatus(com.example.demo.entity.enums.ClaimStatus.OPEN);
        }
        Claim claim = convertToEntity(dto);
        Claim saved = repo.save(claim);
        return convertToDTO(saved);
    }

    // Retrieve all claims from the database
    public List<ClaimDTO> getAllClaims(){
        return repo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Retrieve a claim by its ID with exception details
    public ClaimDTO getClaimById(Long id){
        Claim claim = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim with ID " + id + " not found"));
        return convertToDTO(claim);
    }

  
    // Update only the status of an existing claim
    public ClaimDTO updateClaimStatus(Long id, ClaimStatus status) {
        Claim claim = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim with ID " + id + " not found"));
        claim.setStatus(status);
        Claim updated = repo.save(claim);
        return convertToDTO(updated);
    }

    // Retrieve claims by exception ID with exception details
    public List<ClaimDTO> getClaimByExceptionId(Long exceptionID) {
        return repo.findByExceptionRecord_ExceptionID(exceptionID).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    // Retrieve claims by status
    public List<ClaimDTO> getClaimByStatus(ClaimStatus status) {
        return repo.findByStatus(status).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Convert Entity to DTO with exception details
    private ClaimDTO convertToDTO(Claim claim) {
        ClaimDTO dto = new ClaimDTO();
        dto.setClaimID(claim.getClaimID());
        dto.setFiledBy(claim.getFiledBy());
        dto.setFiledAt(claim.getFiledAt());
        dto.setAmountClaimed(claim.getAmountClaimed());
        dto.setResolutionNotes(claim.getResolutionNotes());
        dto.setStatus(claim.getStatus());
        
        if (claim.getExceptionRecord() != null) {
            dto.setExceptionID(claim.getExceptionRecord().getExceptionID());
            // Fetch full exception details including booking
            try {
                RequiredResponseDTO exceptionDTO = exceptionService.getExceptionById(claim.getExceptionRecord().getExceptionID());
                dto.setException(exceptionDTO);
            } catch (Exception e) {
                System.err.println("Could not fetch exception details for exception ID: " + claim.getExceptionRecord().getExceptionID());
            }
        }
        
        return dto;
    }

    // Convert DTO to Entity
    private Claim convertToEntity(ClaimDTO dto) {
        Claim claim = new Claim();
        claim.setClaimID(dto.getClaimID());
        claim.setFiledBy(dto.getFiledBy());
        claim.setFiledAt(dto.getFiledAt());
        claim.setAmountClaimed(dto.getAmountClaimed());
        claim.setResolutionNotes(dto.getResolutionNotes());
        claim.setStatus(dto.getStatus());
        
        // Fetch and set the exception record
        if (dto.getExceptionID() != null) {
            ExceptionRecord exception = exceptionRepo.findById(dto.getExceptionID())
                    .orElseThrow(() -> new ResourceNotFoundException("Exception with ID " + dto.getExceptionID() + " not found"));
            claim.setExceptionRecord(exception);
        }
        
        return claim;
    }


}