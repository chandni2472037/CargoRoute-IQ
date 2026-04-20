package com.example.demo.controller;

import com.example.demo.dto.VehicleDTO;
import com.example.demo.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargoRoute/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // CREATE
    @PostMapping("/createNewVehicle")
    public VehicleDTO create(@RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.createVehicle(vehicleDTO);
    }

    // READ ALL (with driver + availabilities)
    @GetMapping("/getAllVehicles")
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // READ BY ID (with driver + availabilities)
    @GetMapping("/getVehicle/{id}")
    public VehicleDTO getVehicle(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    // UPDATE
    @PutMapping("/updateVehicle/{id}")
    public VehicleDTO update(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.updateVehicle(id, vehicleDTO);
    }

    // DELETE
    @DeleteMapping("deleteVehicle/{id}")
    public void delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }
}