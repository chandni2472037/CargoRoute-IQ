package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.BillingLineDTO;
import com.example.demo.dto.BillingLineResponseDTO;
import com.example.demo.service.BillingLineService;

@RestController
@RequestMapping("cargoRoute/billing-lines")
public class BillingLineController {

    private final BillingLineService service;

    public BillingLineController(BillingLineService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public BillingLineResponseDTO create(@RequestBody BillingLineDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/getBy/{id}")
    public BillingLineResponseDTO get(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/getAll")
    public List<BillingLineResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/update/{id}")
    public BillingLineResponseDTO update(@PathVariable Long id,
                                         @RequestBody BillingLineDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}