package com.golden.ticket.inventory.controller;

import com.golden.ticket.inventory.model.InventoryQuantityResponse;
import com.golden.ticket.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryController {
    private final InventoryService service;

    @GetMapping("/inventory")
    public InventoryQuantityResponse getInventory() {
        return service.getRemainingQuantity();
    }

    @PostMapping("/buy")
    public ResponseEntity<Void> buyGoldenTicket(@RequestParam String sessionId) {
        service.buyGoldenTicket(sessionId);
        return ResponseEntity.noContent().build();
    }
}
