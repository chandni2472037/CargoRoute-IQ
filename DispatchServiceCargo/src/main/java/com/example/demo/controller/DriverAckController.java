package com.example.demo.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DriverAckDTO;
import com.example.demo.service.DriverAckService;

@RestController
@RequestMapping("/driver-acks")
public class DriverAckController {

    @Autowired
    private DriverAckService driverAckService;

    //CREATE
    @PostMapping
    public ResponseEntity<DriverAckDTO> create(@RequestBody DriverAckDTO dto) {
        return new ResponseEntity<>(driverAckService.insert(dto),HttpStatus.CREATED);
    }

    //FETCH BY ACK ID
    @GetMapping("/{ackID}")
    public ResponseEntity<DriverAckDTO> fetchByID(@PathVariable Long ackID) {
        return ResponseEntity.ok(driverAckService.fetchByID(ackID));
    }

    //FETCH BY DISPATCH ID
    @GetMapping("/dispatch/{dispatchID}")
    public ResponseEntity<DriverAckDTO> fetchByDispatchID(@PathVariable Long dispatchID) {
        return ResponseEntity.ok(driverAckService.fetchByDispatchID(dispatchID));
    }

    //FETCH BY DRIVER ID
    @GetMapping("/driver/{driverID}")
    public ResponseEntity<DriverAckDTO> fetchByDriverID(@PathVariable Long driverID) {
        return ResponseEntity.ok(driverAckService.fetchByDriverID(driverID));
    }

    //FETCH ALL
    @GetMapping
    public ResponseEntity<List<DriverAckDTO>> fetchAll() {
        return ResponseEntity.ok(driverAckService.fetchAll());
    }

    //UPDATE
    @PutMapping("/{ackID}")
    public ResponseEntity<DriverAckDTO> update(@PathVariable Long ackID, @RequestBody DriverAckDTO dto) {
        return ResponseEntity.ok(driverAckService.updateDriverAck(ackID, dto));
    }

    //DELETE
    @DeleteMapping("/{ackID}")
    public ResponseEntity<Void> delete(@PathVariable Long ackID) {
        driverAckService.delete(ackID);
        return ResponseEntity.noContent().build();
    }
}