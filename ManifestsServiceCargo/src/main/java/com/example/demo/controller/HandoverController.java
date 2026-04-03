package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.HandoverDTO;
import com.example.demo.service.HandoverService;

@RestController
@RequestMapping("/handovers")
public class HandoverController {

    @Autowired
    private HandoverService handoverService;

    //Create
    @PostMapping
    public ResponseEntity<HandoverDTO> create(
            @RequestBody HandoverDTO handoverDTO) {

        HandoverDTO created =
                handoverService.create(handoverDTO);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    //Get by ID
    @GetMapping("/{handoverID}")
    public ResponseEntity<HandoverDTO> getById(
            @PathVariable Long handoverID) {

        return ResponseEntity.ok(
                handoverService.getById(handoverID));
    }

    //Get all
    @GetMapping
    public ResponseEntity<List<HandoverDTO>> getAll() {

        return ResponseEntity.ok(
                handoverService.getAll());
    }

    //Get by manifest ID
    @GetMapping("/manifest/{manifestID}")
    public ResponseEntity<HandoverDTO> getByManifestID(
            @PathVariable Long manifestID) {

        return ResponseEntity.ok(
                handoverService.findByManifestID(manifestID));
    }

    //Get by handed by
    @GetMapping("/handedBy/{handedBy}")
    public ResponseEntity<List<HandoverDTO>> getByHandedBy(
            @PathVariable String handedBy) {

        return ResponseEntity.ok(
                handoverService.findByHandedBy(handedBy));
    }

    //Update
    @PutMapping
    public ResponseEntity<HandoverDTO> update(
            @RequestBody HandoverDTO handoverDTO) {

        return ResponseEntity.ok(
                handoverService.update(handoverDTO));
    }

    //Delete
    @DeleteMapping("/{handoverID}")
    public ResponseEntity<Void> delete(
            @PathVariable Long handoverID) {

        handoverService.delete(handoverID);
        return ResponseEntity.noContent().build();
    }
}