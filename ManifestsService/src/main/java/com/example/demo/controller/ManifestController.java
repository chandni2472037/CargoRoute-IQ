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
@RequestMapping("/CargoRoute/manifests")
public class ManifestController {

    @Autowired
    private ManifestService manifestService;

    //CREATE
    @PostMapping("/createManifest")
    public ResponseEntity<ManifestDTO> createManifest(@RequestBody ManifestDTO manifestDTO) {
        return new ResponseEntity<>(manifestService.create(manifestDTO),HttpStatus.CREATED);
    }

    //GET BY ID
    @GetMapping("/getByManifestId/{id}")
    public ResponseEntity<ManifestRequiredResponseDTO> getManifestById(@PathVariable Long id) {
        return ResponseEntity.ok(manifestService.getById(id));
    }

    //GET ALL
    @GetMapping("/getAllManifest")
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getAllManifests() {
        return ResponseEntity.ok(manifestService.getAll());
    }

    //GET BY LOAD ID
    @GetMapping("/getByLoadId/{loadID}")
    public ResponseEntity<ManifestRequiredResponseDTO> getByLoadID(@PathVariable Long loadID) {
        return ResponseEntity.ok(manifestService.getByLoadID(loadID));
    }

    //GET BY WAREHOUSE ID
    @GetMapping("/getByWarehouseId/{warehouseID}")
    public ResponseEntity<List<ManifestRequiredResponseDTO>> getByWarehouseID(@PathVariable Long warehouseID) {
        return ResponseEntity.ok(manifestService.getByWarehouseID(warehouseID));
    }

    //UPDATE
    @PutMapping("/updateManifest/{id}")
    public ResponseEntity<ManifestDTO> updateManifest(@PathVariable Long id, @RequestBody ManifestDTO manifestDTO) {
        return ResponseEntity.ok(manifestService.update(id, manifestDTO));
    }

    //DELETE
    @DeleteMapping("/deleteByManifestId/{id}")
    public ResponseEntity<Void> deleteManifest(@PathVariable Long id) {
        manifestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
