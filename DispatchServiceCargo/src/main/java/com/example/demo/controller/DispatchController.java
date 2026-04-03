package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DispatchDTO;
import com.example.demo.dto.RequiredResponseDTO;
import com.example.demo.entities.enums.DispatchStatus;
import com.example.demo.service.DispatchService;

@RestController
@RequestMapping("/dispatches")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    //CREATE
    @PostMapping
    public ResponseEntity<DispatchDTO> insert(@RequestBody DispatchDTO dto) {

        return new ResponseEntity<>(
                dispatchService.insert(dto),
                HttpStatus.CREATED);
    }

    //GET BY DISPATCH ID
    @GetMapping("/{dispatchID}")
    public ResponseEntity<RequiredResponseDTO> fetchByID(
            @PathVariable Long dispatchID) {

        return ResponseEntity.ok(
                dispatchService.fetchByID(dispatchID));
    }

    //GET BY LOAD ID
    @GetMapping("/load/{loadID}")
    public ResponseEntity<RequiredResponseDTO> fetchByLoadID(
            @PathVariable Long loadID) {

        return ResponseEntity.ok(
                dispatchService.fetchByLoadID(loadID));
    }

    //GET BY DRIVER ID 
    @GetMapping("/driver/{driverID}")
    public ResponseEntity<RequiredResponseDTO> fetchByDriverID(
            @PathVariable Long driverID) {

        return ResponseEntity.ok(
                dispatchService.fetchByAssignedDriverID(driverID));
    }

    //GET BY ASSIGNED BY
    @GetMapping("/assigned-by/{assignedBy}")
    public ResponseEntity<List<RequiredResponseDTO>> fetchByAssignedBy(
            @PathVariable String assignedBy) {

        return ResponseEntity.ok(
                dispatchService.fetchByAssignedBy(assignedBy));
    }

    //GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequiredResponseDTO>> fetchByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                dispatchService.fetchByStatus(
                        DispatchStatus.valueOf(status.toUpperCase())));
    }

    //GET ALL 
    @GetMapping
    public ResponseEntity<List<RequiredResponseDTO>> fetchAll() {

        return ResponseEntity.ok(
                dispatchService.fetchAll());
    }

    //UPDATE
    @PutMapping("/{dispatchID}")
    public ResponseEntity<DispatchDTO> update(
            @PathVariable Long dispatchID,
            @RequestBody DispatchDTO dto) {

        return ResponseEntity.ok(
                dispatchService.updateDispatch(dispatchID, dto));
    }

    //DELETE 
    @DeleteMapping("/{dispatchID}")
    public ResponseEntity<Void> delete(@PathVariable Long dispatchID) {

        dispatchService.delete(dispatchID);
        return ResponseEntity.noContent().build();
    }
}