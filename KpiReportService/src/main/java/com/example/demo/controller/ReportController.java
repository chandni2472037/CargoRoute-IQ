package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ReportDTO;
import com.example.demo.service.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService service;

    // ================= CREATE REPORT =================
    @PostMapping
    public ResponseEntity<ReportDTO> create(@Valid @RequestBody ReportDTO reportDTO) {
        ReportDTO savedReport = service.save(reportDTO);
        return ResponseEntity.ok(savedReport);
    }

    // ================= GET ALL REPORTS =================
    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAll() {
        List<ReportDTO> reports = service.getAll();
        return ResponseEntity.ok(reports);
    }

    // ================= GET REPORT BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getById(@PathVariable Long id) {
        ReportDTO report = service.getById(id);
        return ResponseEntity.ok(report);
    }

    // ================= DELETE REPORT =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Report deleted successfully");
    }
}