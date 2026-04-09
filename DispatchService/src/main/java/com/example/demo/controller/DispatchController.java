package com.example.demo.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.DispatchResponseDTO;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.service.DispatchService;

@RestController
@RequestMapping("/CargoRoute/dispatches")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    /* ================= CREATE ================= */

    @PostMapping("/createDispatch")
    public ResponseEntity<DispatchDTO> createDispatch(
            @RequestBody DispatchDTO dispatchDTO) {

        DispatchDTO created =
                dispatchService.insert(dispatchDTO);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /* ================= FETCH ================= */

    @GetMapping("/getDispatchById/{dispatchId}")
    public ResponseEntity<DispatchResponseDTO> getDispatchById(
            @PathVariable Long dispatchId) {

        return ResponseEntity.ok(
                dispatchService.fetchByID(dispatchId));
    }


    @GetMapping("/getAssigned-by/{assignedBy}")
    public ResponseEntity<List<DispatchResponseDTO>> getDispatchByAssignedBy(
            @PathVariable String assignedBy) {

        return ResponseEntity.ok(
                dispatchService.fetchByAssignedBy(assignedBy));
    }

    @GetMapping("/getDispatchByStatus/{status}")
    public ResponseEntity<List<DispatchResponseDTO>> getDispatchByStatus(
            @PathVariable DispatchStatus status) {

        return ResponseEntity.ok(
                dispatchService.fetchByStatus(status));
    }

    @GetMapping("/getAllDispatches")
    public ResponseEntity<List<DispatchResponseDTO>> getAllDispatches() {

        return ResponseEntity.ok(
                dispatchService.fetchAll());
    }

    /* ================= UPDATE ================= */

    @PutMapping("/updateDispatch/{dispatchId}")
    public ResponseEntity<DispatchDTO> updateDispatch(
            @PathVariable Long dispatchId,
            @RequestBody DispatchDTO dispatchDTO) {

        DispatchDTO updated =
                dispatchService.updateDispatch(dispatchId, dispatchDTO);

        return ResponseEntity.ok(updated);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/DeleteDispatch/{dispatchId}")
    public ResponseEntity<Void> deleteDispatch(
            @PathVariable Long dispatchId) {

        dispatchService.delete(dispatchId);
        return ResponseEntity.noContent().build();
    }
}