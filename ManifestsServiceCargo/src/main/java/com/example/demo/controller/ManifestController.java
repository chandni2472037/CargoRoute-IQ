package com.example.demo.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.service.ManifestService;

@RestController
@RequestMapping("/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    //Create 
    @PostMapping
    public ResponseEntity<ManifestDTO> create(@RequestBody ManifestDTO manifestDTO) {
        ManifestDTO created = manifestService.create(manifestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    //Get by ID
    @GetMapping("/{manifestID}")
    public ResponseEntity<ManifestRequiredResponseDTO> getById(@PathVariable Long manifestID) {
        ManifestRequiredResponseDTO response = manifestService.getById(manifestID);
        return ResponseEntity.ok(response);
    }

    //Get all
    @GetMapping
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getAll() {
        return ResponseEntity.ok(manifestService.getAll());
    }

    //Get by load ID
    @GetMapping("/load/{loadID}")
    public ResponseEntity<ManifestRequiredResponseDTO> getByLoadID(@PathVariable Long loadID) {
        return ResponseEntity.ok(manifestService.getByLoadID(loadID));
    }

    //Get by warehouse ID
    @GetMapping("/warehouse/{warehouseID}")
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getByWarehouseID(@PathVariable Long warehouseID) {
        return ResponseEntity.ok(manifestService.getByWarehouseID(warehouseID));
    }

    //Update
    @PutMapping("/{manifestID}")
    public ResponseEntity<ManifestDTO> update(@PathVariable Long manifestID, @RequestBody ManifestDTO manifestDTO) {
        ManifestDTO updated = manifestService.update(manifestID, manifestDTO);
        return ResponseEntity.ok(updated);
    }

    //Delete
    @DeleteMapping("/{manifestID}")
    public ResponseEntity<Void> delete(@PathVariable Long manifestID) {
        manifestService.delete(manifestID);
        return ResponseEntity.noContent().build();
    }
}