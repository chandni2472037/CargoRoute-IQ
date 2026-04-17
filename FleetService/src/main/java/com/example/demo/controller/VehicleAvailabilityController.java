package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.entity.VehicleAvailability;
import com.example.demo.service.VehicleAvailabilityService;
//import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/cargoRoute/vehicleAvailability")
public class VehicleAvailabilityController {

    @Autowired
    private VehicleAvailabilityService service;

    // CREATE
    @PostMapping("/createNewVehicleAvailability")
//    @PreAuthorize("hasRole('FleetManager')")
    public VehicleAvailability create(@RequestBody VehicleAvailability v) {
        return service.save(v);
    }

    // READ ALL
    @GetMapping("/getAllVehicleAvailabilities")
    public List<VehicleAvailability> getAll() {
        return service.getAll();
    }

    // READ BY ID
    @GetMapping("/getVehicleAvailability/{id}")
    public VehicleAvailability get(@PathVariable Long id) {
        return service.getById(id);
    }
 // READ BY VEHICLE ID (new endpoint)
    @GetMapping("/vehicle/{vehicleId}")
    public List<VehicleAvailability> getByVehicle(@PathVariable Long vehicleId) {
        return service.getByVehicleId(vehicleId);
    }



    // UPDATE
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('FleetManager')")
    public VehicleAvailability update(@PathVariable Long id, @RequestBody VehicleAvailability v) {
        return service.update(id, v);
    }

    // DELETE
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('FleetManager')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}