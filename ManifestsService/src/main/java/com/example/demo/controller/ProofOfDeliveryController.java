package com.example.demo.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProofOfDeliveryDTO;
import com.example.demo.dto.ProofOfDeliveryResponseDTO;
import com.example.demo.entities.enums.PodType;
import com.example.demo.entities.enums.ProofOfDeliveryStatus;
import com.example.demo.service.ProofOfDeliveryService;

@RestController
@RequestMapping("/cargoRoute/proof-of-delivery")
public class ProofOfDeliveryController {

    @Autowired
    private ProofOfDeliveryService service;

    // CREATE
    @PostMapping("/createProofOfDelivery")
    public ResponseEntity<Map<String, String>> create(
            @RequestBody ProofOfDeliveryDTO dto) {

        service.create(dto);
        return new ResponseEntity<>(
        		Map.of("message", "Proof of Delivery created successfully."),HttpStatus.CREATED);
    }

    // GET BY POD ID
    @GetMapping("/getById/{podId}")
    public ResponseEntity<ProofOfDeliveryResponseDTO> getById(
            @PathVariable Long podId) {
        return ResponseEntity.ok(service.getById(podId));
    }

    // GET ALL
    @GetMapping("/getAllProofOfDeliveries")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET BY BOOKING
    @GetMapping("/getByBookingId/{bookingId}")
    public ResponseEntity<ProofOfDeliveryResponseDTO> getByBooking(@PathVariable Long bookingId) {

        return ResponseEntity.ok(service.getByBookingID(bookingId));
    }

    // GET BY POD TYPE
    @GetMapping("/getByType/{podType}")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getByPodType(@PathVariable PodType podType) {
    	
        return ResponseEntity.ok(service.getByPodType(podType));
    }

    // GET BY POD STATUS
    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getByStatus(@PathVariable ProofOfDeliveryStatus status) {

        return ResponseEntity.ok(service.getByProofOfDeliveryStatus(status));
    }

    // UPDATE
    @PutMapping("/updateProofOfDelivery/{podId}")
    public ResponseEntity<Map<String, String>> update(@PathVariable Long podId,@RequestBody ProofOfDeliveryDTO dto) {

        service.update(podId, dto);
        return ResponseEntity.ok(
                Map.of("message", "Proof of Delivery updated successfully."));
    }

    // DELETE
    @DeleteMapping("/deleteById/{podId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long podId) {

        service.delete(podId);
        return ResponseEntity.ok(
                Map.of("message", "Proof of Delivery deleted successfully."));
    }
}