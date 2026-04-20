package com.example.demo.controller;

import java.util.List;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.KPIDTO;
import com.example.demo.service.KPIService;

@RestController
@RequestMapping("/kpis")
public class KPIController {

    @Autowired
    private KPIService service;

    // ================= CREATE KPI =================
    @PostMapping
    public ResponseEntity<KPIDTO> create(@Valid @RequestBody KPIDTO kpiDTO) {
        KPIDTO savedKPI = service.save(kpiDTO);
        return ResponseEntity.ok(savedKPI);
    }

    // ================= GET ALL KPIs =================
    @GetMapping
    public ResponseEntity<List<KPIDTO>> getAll() {
        List<KPIDTO> kpis = service.getAll();
        return ResponseEntity.ok(kpis);
    }

    // ================= GET KPI BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<KPIDTO> getById(@PathVariable Long id) {
        KPIDTO kpi = service.getById(id);
        return ResponseEntity.ok(kpi);
    }

    // ================= DELETE KPI =================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("KPI deleted successfully");
    }
}
