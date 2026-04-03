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

import com.example.demo.dto.ExceptionRecordDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entity.enums.ExceptionStatus;
import com.example.demo.service.ExceptionService;

@RestController 
@RequestMapping("/exceptions") 
public class ExceptionController {

    @Autowired
    private ExceptionService service; 

    @PostMapping
    // Create a new ExceptionRecord
    public ResponseEntity<ExceptionRecordDTO> addException(@RequestBody ExceptionRecordDTO e) {
        ExceptionRecordDTO saved = service.createException(e); 
        return new ResponseEntity<>(saved, HttpStatus.CREATED); 
    }

    @GetMapping
    // Retrieve all ExceptionRecord entries
    public ResponseEntity<List<RequiredResponseDTO>> fetchAllExceptions() {
        return ResponseEntity.ok(service.getAllExceptions()); // 200 OK with the list of exceptions
    }

    @GetMapping("/{id}")
    // Retrieve a single ExceptionRecord by ID with booking details
    public ResponseEntity<RequiredResponseDTO> fetchExceptionById(@PathVariable Long id) {
        RequiredResponseDTO exception = service.getExceptionById(id); // Service returns entity or throws exception
        return new ResponseEntity<>(exception,HttpStatus.OK); // 200 OK if found
    }

  
    
    @PatchMapping("/{id}/status")
    // Partially update the exception: only the status field
    public ResponseEntity<ExceptionRecordDTO> modifyExceptionStatus(@PathVariable Long id, @RequestParam ExceptionStatus status) {
        ExceptionRecordDTO updated = service.updateExceptionStatus(id, status); // Delegate status change to service
        return ResponseEntity.ok(updated); // 200 OK with updated exception
    }

    @GetMapping("/booking/{bookingId}")
    // Retrieve all exceptions for a specific booking with booking details
    public ResponseEntity<List<RequiredResponseDTO>> fetchExceptionByBookingId(@PathVariable Long bookingId) {
        List<RequiredResponseDTO> exceptions = service.getExceptionByBookingId(bookingId);
        return ResponseEntity.ok(exceptions); // 200 OK with list of exceptions
    }

    @GetMapping("/status/{status}")
    // Retrieve all exceptions by status
    public ResponseEntity<List<RequiredResponseDTO>> fetchExceptionByStatus(@PathVariable ExceptionStatus status) {
        List<RequiredResponseDTO> exceptions = service.getExceptionByStatus(status);
        return ResponseEntity.ok(exceptions); // 200 OK with list of exceptions
    }
}
