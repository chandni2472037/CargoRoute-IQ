package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoadDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.service.LoadService;

@RestController
@RequestMapping("/loads")
public class LoadController {

    @Autowired
    private LoadService loadService;

    @GetMapping("/{id}")
    public ResponseEntity<RequiredResponseDTO> getLoadById(@PathVariable Long id) {
        return ResponseEntity.ok(loadService.getLoadById(id));
    }

    @GetMapping
    public ResponseEntity<List<RequiredResponseDTO>> getAllLoads() {
        return ResponseEntity.ok(loadService.getAllLoads());
    }

    @PostMapping
    public ResponseEntity<LoadDTO> createLoad(@RequestBody LoadDTO loadDTO) {
        return ResponseEntity.ok(loadService.createLoad(loadDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoadDTO> updateLoad(@PathVariable Long id, @RequestBody LoadDTO loadDTO) {
        return ResponseEntity.ok(loadService.updateLoad(id, loadDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoad(@PathVariable Long id) {
        loadService.deleteLoad(id);
        return ResponseEntity.noContent().build();
    }

   
}