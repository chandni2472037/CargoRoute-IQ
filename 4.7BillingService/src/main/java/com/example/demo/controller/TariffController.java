package com.example.demo.controller;

import java.util.List;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.TariffDTO;
import com.example.demo.service.TariffService;

@RestController
@RequestMapping("cargoRoute/tariffs")
public class TariffController {

    @Autowired
    private TariffService service;

    // ================= CREATE =================
    @PostMapping("/create")
    public ResponseEntity<TariffDTO> createTariff(
            @Valid @RequestBody TariffDTO tariffDTO) {

        return ResponseEntity.ok(service.save(tariffDTO));
    }

    // ================= GET ALL =================
    @GetMapping("/getAll")
    public ResponseEntity<List<TariffDTO>> getAllTariffs() {
        return ResponseEntity.ok(service.getAll());
    }

    // ================= GET BY ID =================
    @GetMapping("/getBy/{id}")
    public ResponseEntity<TariffDTO> getTariffById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ================= UPDATE =================
    @PutMapping("/update/{id}")
    public ResponseEntity<TariffDTO> updateTariff(
            @PathVariable Long id,
            @Valid @RequestBody TariffDTO tariffDTO) {

        return ResponseEntity.ok(service.update(id, tariffDTO));
    }

    // ================= DELETE =================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTariff(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Tariff deleted successfully");
    }
}