package com.inventory.inventoryservice;

import com.inventory.inventoryservice.service.InventoryServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryServiceServiceTest {

    private InventoryServiceService service;

    @BeforeEach
    void setUp() {
        service = new InventoryServiceService();
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(service);
    }
}
