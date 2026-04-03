package com.example.demo.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DriverDTO;
import com.example.demo.entities.enums.DriverStatus;
import com.example.demo.service.DriverService;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    //CREATE
    @PostMapping
    public ResponseEntity<DriverDTO> create(@RequestBody DriverDTO dto) {
        return new ResponseEntity<>(
                driverService.insert(dto),
                HttpStatus.CREATED);
    }

    //GET BY ID 
    @GetMapping("/{driverID}")
    public ResponseEntity<DriverDTO> getById(@PathVariable Long driverID) {
        return ResponseEntity.ok(
                driverService.fetchByID(driverID));
    }

    //GET BY STATUS 
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverDTO>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                driverService.fetchByStatus(
                        DriverStatus.valueOf(status.toUpperCase())));
    }

    //GET ALL 
    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAll() {
        return ResponseEntity.ok(
                driverService.fetchAll());
    }

    //UPDATE 
    @PutMapping("/{driverID}")
    public ResponseEntity<DriverDTO> update(
            @PathVariable Long driverID,
            @RequestBody DriverDTO dto) {

        return ResponseEntity.ok(
                driverService.updateDriver(driverID, dto));
    }

    // DELETE
    @DeleteMapping("/{driverID}")
    public ResponseEntity<Void> delete(@PathVariable Long driverID) {
        driverService.delete(driverID);
        return ResponseEntity.noContent().build();
    }
}