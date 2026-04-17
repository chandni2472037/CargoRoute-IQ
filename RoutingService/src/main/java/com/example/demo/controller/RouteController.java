package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RouteDTO;
import com.example.demo.service.RouteService;

@RestController
@RequestMapping("/cargoRoute/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/getRoute/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/getAllRoutes")
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @PostMapping("/createNewRoute")
    public ResponseEntity<RouteDTO> createRoute(@RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.createRoute(routeDTO));
    }

    @PutMapping("/updateRoute/{id}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Long id, @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.updateRoute(id, routeDTO));
    }

    @DeleteMapping("/deleteRoute/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<RouteDTO>> getRoutesByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(routeService.getRoutesByVehicleId(vehicleId));
    }

    @GetMapping("/load/{loadId}")
    public ResponseEntity<List<RouteDTO>> getRoutesByLoadId(@PathVariable Long loadId) {
        return ResponseEntity.ok(routeService.getRoutesByLoadId(loadId));
    }
}