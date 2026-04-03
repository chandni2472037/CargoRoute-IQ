package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ShipperDTO;
import com.example.demo.entity.enums.ShipperStatus;
import com.example.demo.service.ShipperService;

@RestController
@RequestMapping("/shippers")
public class ShipperController {

    @Autowired
    private ShipperService service;

    @PostMapping
    public ResponseEntity<ShipperDTO> addShipper(@RequestBody ShipperDTO s) {
        ShipperDTO savedShipper = service.createShipper(s);
        return new ResponseEntity<>(savedShipper, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping
    public ResponseEntity<List<ShipperDTO>> fetchAllShippers() {
        List<ShipperDTO> shippers = service.getAllShippers();
        return ResponseEntity.ok(shippers); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipperDTO> fetchShipperById(@PathVariable Long id) {
        ShipperDTO shipper = service.getShipperById(id);
        return (shipper != null) ? ResponseEntity.ok(shipper) : ResponseEntity.notFound().build(); // 200 or 404
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipperDTO> modifyShipper(@PathVariable Long id, @RequestBody ShipperDTO updated) {
        ShipperDTO existing = service.getShipperById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        updated.setShipperID(id); // ensure ID stays consistent
        ShipperDTO saved = service.createShipper(updated);
        return ResponseEntity.ok(saved); // 200 OK
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShipperDTO>> fetchByShipperStatus(@PathVariable ShipperStatus status) {
        List<ShipperDTO> shippers = service.getByShipperStatus(status);
        return ResponseEntity.ok(shippers);
    }

   
}