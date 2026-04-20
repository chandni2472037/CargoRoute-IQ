package com.example.demo.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.DispatchResponseDTO;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.service.DispatchService;

@RestController
@RequestMapping("/cargoRoute/dispatches")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    //CREATE 
    @PostMapping("/createDispatch")
    public ResponseEntity<Map<String, String>> createDispatch(
            @RequestBody DispatchDTO dispatchDTO) {

        dispatchService.insert(dispatchDTO);

        return new ResponseEntity<>(
                Map.of("message", "Dispatch created successfully."),
                HttpStatus.CREATED
        );
    }

    //FETCH
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

    //UPDATE
    @PutMapping("/updateDispatch/{dispatchId}")
    public ResponseEntity<Map<String, String>> updateDispatch(
            @PathVariable Long dispatchId,
            @RequestBody DispatchDTO dispatchDTO) {

        dispatchService.updateDispatch(dispatchId, dispatchDTO);

        return ResponseEntity.ok(
                Map.of("message", "Dispatch updated successfully.")
        );
    }

    //DELETE
    @DeleteMapping("/deleteDispatch/{dispatchId}")
    public ResponseEntity<Map<String, String>> deleteDispatch(
            @PathVariable Long dispatchId) {

        dispatchService.delete(dispatchId);

        return ResponseEntity.ok(
                Map.of("message", "Dispatch deleted successfully.")
        );
    }
}