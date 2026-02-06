package com.golden.ticket.inventory;

import com.golden.ticket.inventory.exception.OutOfQuantityException;
import com.golden.ticket.inventory.model.InventoryQuantityResponse;
import com.golden.ticket.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("UPDATE inventory SET quantity = 100 WHERE id = 1");
    }

    @Test
    void shouldReturnRemainingQuantity() {
        InventoryQuantityResponse response = inventoryService.getRemainingQuantity();
        assertThat(response.remainingQuantity()).isEqualTo(100L);
    }

    @Test
    void shouldBuyGoldenTicketSuccessfully() {
        inventoryService.buyGoldenTicket("session-1");

        InventoryQuantityResponse response = inventoryService.getRemainingQuantity();
        assertThat(response.remainingQuantity()).isEqualTo(99L);
    }

    @Test
    void shouldThrowExceptionWhenOutOfQuantity() {
        for (int i = 0; i < 100; i++) {
            inventoryService.buyGoldenTicket("session-" + i);
        }

        assertThatThrownBy(() -> inventoryService.buyGoldenTicket("session-fail"))
                .isInstanceOf(OutOfQuantityException.class)
                .hasMessage("No Golden Tickets remaining.");
    }

    @Test
    void  shouldHandleHighConcurrency() throws InterruptedException {
        int ticketCount = 100;
        int threadCount = 150;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String sessionId = "session-" + i;
            executorService.execute(() -> {
                try {
                    inventoryService.buyGoldenTicket(sessionId);
                    successCount.incrementAndGet();
                } catch (OutOfQuantityException e) {
                    failureCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Verify
        assertThat(successCount.get()).as("Successful purchases").isEqualTo(ticketCount);
        assertThat(failureCount.get()).as("Failed purchases").isEqualTo(threadCount - ticketCount);

        InventoryQuantityResponse response = inventoryService.getRemainingQuantity();
        assertThat(response.remainingQuantity()).as("Remaining quantity").isEqualTo(0L);
    }
}
