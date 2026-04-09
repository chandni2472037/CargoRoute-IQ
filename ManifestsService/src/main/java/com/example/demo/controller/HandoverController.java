package com.example.demo.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.HandoverDTO;
import com.example.demo.dto.HandoverResponseDTO;
import com.example.demo.service.HandoverService;

@RestController
@RequestMapping("/CargoRoute/handovers")
public class HandoverController {

    @Autowired
    private HandoverService handoverService;


    @PostMapping("/createHandover")
    public ResponseEntity<HandoverDTO> create(@RequestBody HandoverDTO dto) {
        return new ResponseEntity<>(handoverService.create(dto),HttpStatus.CREATED);
    }


    //FETCH
    @GetMapping("/getByHandoverId/{id}")
    public ResponseEntity<HandoverResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(handoverService.getById(id));
    }

    @GetMapping("/getAllHandovers")
    public ResponseEntity<List<HandoverResponseDTO>> getAll() {
        return ResponseEntity.ok(handoverService.getAll());
    }

    @GetMapping("/getByManifestId/{manifestID}")
    public ResponseEntity<HandoverResponseDTO> getByManifestID(
            @PathVariable Long manifestID) {
        return ResponseEntity.ok(
                handoverService.getByManifestID(manifestID));
    }

    @GetMapping("/getHanded-by/{handedBy}")
    public ResponseEntity<List<HandoverResponseDTO>> getByHandedBy(
            @PathVariable String handedBy) {
        return ResponseEntity.ok(
                handoverService.getByHandedBy(handedBy));
    }

    //UPDATE
    @PutMapping("/updateHandover/{handoverId}")
    public ResponseEntity<HandoverDTO> update(
            @PathVariable Long handoverId,
            @RequestBody HandoverDTO dto) {

        return ResponseEntity.ok(
                handoverService.update(handoverId, dto));
    }

    //DELETE
    @DeleteMapping("/deleteByHandoverId/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        handoverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}