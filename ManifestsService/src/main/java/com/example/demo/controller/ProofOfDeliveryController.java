package com.example.demo.controller;
import java.util.List;
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
@RequestMapping("/CargoRoute/proof-of-delivery")
public class ProofOfDeliveryController {

    @Autowired
    private ProofOfDeliveryService service;

    //CREATE
    @PostMapping("/createProofOfDelivery")
    public ResponseEntity<ProofOfDeliveryDTO> create(@RequestBody ProofOfDeliveryDTO dto) {
        
    	return new ResponseEntity<>(service.create(dto),
                HttpStatus.CREATED);
    }

    //GET BY POD ID
    @GetMapping("/getById/{podId}")
    public ResponseEntity<ProofOfDeliveryResponseDTO> getById(@PathVariable Long podId) {
        
    	return new ResponseEntity<>(
                service.getById(podId),
                HttpStatus.OK);
    }

    //GET ALL
    @GetMapping("/getAllProofOfDeliveries")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getAll() {
        
    	return new ResponseEntity<>(
                service.getAll(),
                HttpStatus.OK);
    }

    //GET BY BOOKING
    @GetMapping("/getByBookingId/{bookingId}")
    public ResponseEntity<ProofOfDeliveryResponseDTO> getByBooking(@PathVariable Long bookingId) {
        
    	return new ResponseEntity<>(
                service.getByBookingID(bookingId),
                HttpStatus.OK);
    }

    //GET BY POD TYPE
    @GetMapping("/GetByType/{podType}")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getByPodType(@PathVariable PodType podType) {
        
    	return new ResponseEntity<>(
                service.getByPodType(podType),
                HttpStatus.OK);
    }

    //GET BY POD STATUS
    @GetMapping("/getByStatus/{status}")
    public ResponseEntity<List<ProofOfDeliveryResponseDTO>> getByStatus(@PathVariable ProofOfDeliveryStatus status) {

        return new ResponseEntity<>(
                service.getByProofOfDeliveryStatus(status),
                HttpStatus.OK);
    }

    //UPDATE
    @PutMapping("/updateProofOfDelivery/{podId}")
    public ResponseEntity<ProofOfDeliveryDTO> update(@PathVariable Long podId, @RequestBody ProofOfDeliveryDTO dto) {

        return new ResponseEntity<>(
                service.update(podId, dto),
                HttpStatus.OK);
    }

    //DELETE

    @DeleteMapping("/deleteById/{podId}")
    public ResponseEntity<Void> delete(@PathVariable Long podId) {
        service.delete(podId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}