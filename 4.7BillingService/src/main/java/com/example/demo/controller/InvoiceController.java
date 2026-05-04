package com.example.demo.controller;
 
import java.util.List;
 
import jakarta.validation.Valid;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
 
import com.example.demo.service.InvoiceService;

import com.example.demo.dto.InvoiceDTO;

import com.example.demo.dto.InvoiceRequiredResponseDTO;
 
@RestController

@RequestMapping("cargoRoute/invoices")

public class InvoiceController {
 
    @Autowired

    private InvoiceService service;
 
    // ✅ CREATE — BillingClerk or Admin

    @PreAuthorize("hasAnyRole('BILLINGCLERK','ADMIN')")

    @PostMapping("/create")

    public ResponseEntity<InvoiceDTO> create(

            @Valid @RequestBody InvoiceDTO invoiceDTO) {

        return ResponseEntity.ok(service.save(invoiceDTO));

    }
 
    // ✅ GET ALL — BillingClerk, Admin, Shipper, Analyst

    @PreAuthorize("hasAnyRole('BILLINGCLERK','ADMIN','SHIPPER','ANALYST')")

    @GetMapping("/getAll")

    public ResponseEntity<List<InvoiceRequiredResponseDTO>> getAll() {

        return ResponseEntity.ok(service.getAll());

    }
 
    // ✅ GET BY ID — BillingClerk, Admin, Shipper, Analyst

    @PreAuthorize("hasAnyRole('BILLINGCLERK','ADMIN','SHIPPER','ANALYST')")

    @GetMapping("/getBy/{id}")

    public ResponseEntity<InvoiceRequiredResponseDTO> getById(

            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));

    }
 
    // ✅ UPDATE — BillingClerk or Admin

    @PreAuthorize("hasAnyRole('BILLINGCLERK','ADMIN')")

    @PutMapping("/update/{id}")

    public ResponseEntity<InvoiceDTO> update(

            @PathVariable Long id,

            @Valid @RequestBody InvoiceDTO invoiceDTO) {

        return ResponseEntity.ok(service.update(id, invoiceDTO));

    }
 
    // ✅ DELETE — Admin only

    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/delete/{id}")

    public ResponseEntity<String> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok("Invoice deleted successfully");

    }

}

 