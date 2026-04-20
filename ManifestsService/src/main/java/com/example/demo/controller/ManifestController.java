package com.example.demo.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ManifestDTO;
import com.example.demo.dto.ManifestRequiredResponseDTO;
import com.example.demo.service.ManifestService;

@RestController
@RequestMapping("/cargoRoute/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    // CREATE
    @PostMapping("/createManifest")
    public ResponseEntity<Map<String, String>> createManifest(
            @RequestBody ManifestDTO manifestDTO) {

        manifestService.create(manifestDTO);
        return new ResponseEntity<>(
                Map.of("message", "Manifest created successfully."),
                HttpStatus.CREATED
        );
    }

    // GET BY ID
    @GetMapping("/getByManifestId/{id}")
    public ResponseEntity<ManifestRequiredResponseDTO> getManifestById(@PathVariable Long id) {
        return ResponseEntity.ok(manifestService.getById(id));
    }

    // GET ALL
    @GetMapping("/getAllManifest")
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getAllManifests() {
        return ResponseEntity.ok(manifestService.getAll());
    }

    // GET BY LOAD ID
    @GetMapping("/getByLoadId/{loadID}")
    public ResponseEntity<ManifestRequiredResponseDTO> getByLoadID(@PathVariable Long loadID) {
        return ResponseEntity.ok(manifestService.getByLoadID(loadID));
    }

    // GET BY WAREHOUSE ID
    @GetMapping("/getByWarehouseId/{warehouseID}")
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getByWarehouseID(@PathVariable Long warehouseID) {
        return ResponseEntity.ok(manifestService.getByWarehouseID(warehouseID));
    }

    // UPDATE
    @PutMapping("/updateManifest/{id}")
    public ResponseEntity<Map<String, String>> updateManifest(
            @PathVariable Long id,
            @RequestBody ManifestDTO manifestDTO) {

        manifestService.update(id, manifestDTO);
        return ResponseEntity.ok(
                Map.of("message", "Manifest updated successfully.")
        );
    }

    // DELETE
    @DeleteMapping("/deleteByManifestId/{id}")
    public ResponseEntity<Map<String, String>> deleteManifest(@PathVariable Long id) {

        manifestService.delete(id);
        return ResponseEntity.ok(
                Map.of("message", "Manifest deleted successfully.")
        );
    }
}