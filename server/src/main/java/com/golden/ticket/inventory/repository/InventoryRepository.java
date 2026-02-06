package com.golden.ticket.inventory.repository;

import com.golden.ticket.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Modifying
    @Transactional
    @Query(value = """
                    UPDATE inventory i
                    SET i.quantity = i.quantity - 1
                    WHERE i.id = :id AND i.quantity > 0
            """, nativeQuery = true)
    int decrementQuantityIfAvailable(Long id);

    @Query(value = "SELECT i.quantity FROM inventory i WHERE i.id = :id", nativeQuery = true)
    Long getRemainingQuantity(Long id);
}
