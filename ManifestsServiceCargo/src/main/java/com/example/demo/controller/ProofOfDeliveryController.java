package com.example.demo.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.service.ProofOfDeliveryService;

@RestController
@RequestMapping("/pods")
public class ProofOfDeliveryController {

    @Autowired
    private ProofOfDeliveryService proofOfDeliveryService;

    //Create
    @PostMapping
    public ResponseEntity<ProofOfDeliveryDTO> create(@RequestBody ProofOfDeliveryDTO dto) {
        return new ResponseEntity<>(proofOfDeliveryService.create(dto), HttpStatus.CREATED);
    }

    //Get by ID
    @GetMapping("/{podID}")
    public ResponseEntity<RequiredResponseDTO> getById(@PathVariable Long podID) {
        return ResponseEntity.ok(proofOfDeliveryService.getById(podID));
    }

    //Get all
    @GetMapping
    public ResponseEntity<List<RequiredResponseDTO>> getAll() {
        return ResponseEntity.ok(proofOfDeliveryService.getAll());
    }

    //Get by booking ID
    @GetMapping("/booking/{bookingID}")
    public ResponseEntity<RequiredResponseDTO> getByBookingID(@PathVariable Long bookingID) {
        return ResponseEntity.ok(proofOfDeliveryService.getByBookingID(bookingID));
    }

    //Get by POD type
    @GetMapping("/type/{podType}")
    public ResponseEntity<List<RequiredResponseDTO>> getByPodType(@PathVariable PodType podType) {
        return ResponseEntity.ok(proofOfDeliveryService.getByPodType(podType));
    }

    //Get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequiredResponseDTO>> getByStatus(@PathVariable ProofOfDeliveryStatus status) {
        return ResponseEntity.ok(proofOfDeliveryService.getByProofOfDeliveryStatus(status));
    }

    //Update
    @PutMapping("/{podID}")
    public ResponseEntity<ProofOfDeliveryDTO> update(@PathVariable Long podID, @RequestBody ProofOfDeliveryDTO dto) {
        return ResponseEntity.ok(proofOfDeliveryService.update(podID, dto));
    }

    //Delete
    @DeleteMapping("/{podID}")
    public ResponseEntity<Void> delete(@PathVariable Long podID) {
        proofOfDeliveryService.delete(podID);
        return ResponseEntity.noContent().build();
    }
}