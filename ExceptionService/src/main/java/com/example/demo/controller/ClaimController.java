package com.example.demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ClaimDTO;
import com.example.demo.entity.enums.ClaimStatus;
import com.example.demo.service.ClaimService;

@RestController // Marks this as a REST controller (methods return JSON objects)
@RequestMapping("/claims") // Base path for all Claim-related endpoints
public class ClaimController {

    @Autowired
    private ClaimService service; // Injects the ClaimService to handle business logic

    @PostMapping
    // Create a new Claim record
    public ResponseEntity<ClaimDTO> addClaim(@RequestBody ClaimDTO c) {
        ClaimDTO saved = service.createClaim(c); // Delegate creation to service layer
        return new ResponseEntity<>(saved, HttpStatus.CREATED); // Respond with 201 Created
    }

    @GetMapping
    // Retrieve all Claim records with exception details
    public ResponseEntity<List<ClaimDTO>> fetchAllClaims() {
        return ResponseEntity.ok(service.getAllClaims()); // 200 OK with list of claims
    }

    @GetMapping("/{id}")
    // Retrieve a specific Claim by ID with exception details
    public ResponseEntity<ClaimDTO> fetchClaimById(@PathVariable Long id) {
        ClaimDTO claim = service.getClaimById(id); // Fetch record
        return ResponseEntity.ok(claim); // Return 200 OK if found
    }

    
    @PatchMapping("/{id}/status")
    // Update **only** the status field of the Claim
    public ResponseEntity<ClaimDTO> modifyClaimStatus(@PathVariable Long id, @RequestParam ClaimStatus status) { // Accept status as query parameter
        
        ClaimDTO updated = service.updateClaimStatus(id, status); // Delegate update to service
        return ResponseEntity.ok(updated); // 200 OK with updated claim
    }

    @GetMapping("/exception/{exceptionId}")
    // Retrieve all claims for a specific exception with exception details
    public ResponseEntity<List<ClaimDTO>> fetchClaimByExceptionId(@PathVariable Long exceptionId) {
        List<ClaimDTO> claims = service.getClaimByExceptionId(exceptionId);
        return ResponseEntity.ok(claims); // 200 OK with list of claims
    }

    @GetMapping("/status/{status}")
    // Retrieve all claims by status
    public ResponseEntity<List<ClaimDTO>> fetchByClaimStatus(@PathVariable ClaimStatus status) {
        List<ClaimDTO> claims = service.getClaimByStatus(status);
        return ResponseEntity.ok(claims); // 200 OK with list of claims
    }
}