package com.golden.ticket.inventory.model;

import jakarta.validation.constraints.NotNull;

public record InventoryQuantityResponse(@NotNull Long remainingQuantity) {
}
