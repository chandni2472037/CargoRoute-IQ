package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.RoutingRuleDTO;
import com.example.demo.service.RoutingRuleService;

@RestController
@RequestMapping("/routingRules")
public class RoutingRuleController {

    @Autowired
    private RoutingRuleService ruleService;

    @GetMapping("/{id}")
    public ResponseEntity<RoutingRuleDTO> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoutingRuleDTO>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping
    public ResponseEntity<RoutingRuleDTO> createRule(@RequestBody RoutingRuleDTO ruleDTO) {
        return ResponseEntity.ok(ruleService.createRule(ruleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutingRuleDTO> updateRule(@PathVariable Long id, @RequestBody RoutingRuleDTO ruleDTO) {
        return ResponseEntity.ok(ruleService.updateRule(id, ruleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<RoutingRuleDTO>> getActiveRules() {
        return ResponseEntity.ok(ruleService.getActiveRules());
    }
}