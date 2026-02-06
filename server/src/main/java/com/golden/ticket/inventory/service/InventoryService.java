package com.golden.ticket.inventory.service;

import com.golden.ticket.inventory.exception.OutOfQuantityException;
import com.golden.ticket.inventory.model.InventoryQuantityResponse;
import com.golden.ticket.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository repository;

    public InventoryQuantityResponse getRemainingQuantity() {
        return new InventoryQuantityResponse(repository.getRemainingQuantity(1L));
    }

    public void buyGoldenTicket(String sessionId) {
        log.info("Session {} is attempting to buy a Golden Ticket.", sessionId);
        var result = repository.decrementQuantityIfAvailable(1L);

        if (result == 0) {
            log.warn("Session {} failed to purchase a Golden Ticket. No tickets remaining.", sessionId);
            throw new OutOfQuantityException("No Golden Tickets remaining.");
        }

        log.info("Session {} successfully purchased a Golden Ticket.", sessionId);
    }
}
