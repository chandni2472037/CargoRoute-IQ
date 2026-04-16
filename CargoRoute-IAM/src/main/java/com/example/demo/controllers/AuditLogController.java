package com.example.demo.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DTO.AuditLogDTO;
import com.example.demo.services.AuditLogService;

/**
 * AuditLogController
 * ------------------
 * Handles HTTP requests related to audit logs.
 * Typically restricted to ADMIN users.
 */
@RestController
@RequestMapping("/cargoRoute/auditLogs")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    /** Create audit log */
    @PostMapping("/create")
    public ResponseEntity<AuditLogDTO> createLog(@RequestBody AuditLogDTO dto) {
        return new ResponseEntity<>(service.saveAuditLog(dto), HttpStatus.CREATED);
    }

    /** Get all audit logs */
    @GetMapping("/getAllAuditLogs")
    public ResponseEntity<List<AuditLogDTO>> getAllLogs() {
        return ResponseEntity.ok(service.getAllAuditLogs());
    }

    /** Get audit log by ID */
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDTO> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAuditLogById(id));
    }
}