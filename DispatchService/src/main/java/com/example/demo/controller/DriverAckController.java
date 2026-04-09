package com.example.demo.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.DriverAckDTO;
import com.example.demo.dto.DriverAckResponseDTO;
import com.example.demo.service.DriverAckService;

@RestController
@RequestMapping("/CargoRoute/driver-acknowledgement")
public class DriverAckController {

    @Autowired
    private DriverAckService driverAckService;

    /* ================= CREATE ================= */

    @PostMapping("/createDriverAcknowledgement")
    public ResponseEntity<DriverAckDTO> create(
            @RequestBody DriverAckDTO dto) {

        return new ResponseEntity<>(
                driverAckService.insert(dto),
                HttpStatus.CREATED);
    }

    /* ================= FETCH ================= */

    @GetMapping("/getByAckId/{ackId}")
    public ResponseEntity<DriverAckResponseDTO> getById(@PathVariable Long ackId) {
        return ResponseEntity.ok(
                driverAckService.fetchByID(ackId));
    }

    @GetMapping("/getByDispatchId/{dispatchId}")
    public ResponseEntity<DriverAckResponseDTO> getByDispatch(@PathVariable Long dispatchId) {
        return ResponseEntity.ok(driverAckService.fetchByDispatchID(dispatchId));
    }


    @GetMapping("/getByDriverId/{driverId}")
    public ResponseEntity<DriverAckResponseDTO> getByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverAckService.fetchByDriverID(driverId));
    }

   
    @GetMapping("/getAllDriverAcknowledgement")
    public ResponseEntity<List<DriverAckResponseDTO>> getAll() {
        return ResponseEntity.ok(driverAckService.fetchAll());
    }

    /* ================= UPDATE ================= */

    @PutMapping("/updateDriverAcknowledgement/{ackId}")
    public ResponseEntity<DriverAckDTO> update(
            @PathVariable Long ackId,
            @RequestBody DriverAckDTO dto) {

        return ResponseEntity.ok(
                driverAckService.updateDriverAck(ackId, dto));
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/deleteDriverAck/{ackId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ackId) {

        driverAckService.delete(ackId);
        return ResponseEntity.noContent().build();
    }
}

