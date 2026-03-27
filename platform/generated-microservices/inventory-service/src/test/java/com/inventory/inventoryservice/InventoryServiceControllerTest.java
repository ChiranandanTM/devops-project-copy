package com.inventory.inventoryservice;

import com.inventory.inventoryservice.controller.InventoryServiceController;
import com.inventory.inventoryservice.service.InventoryServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryServiceControllerTest {

    @Mock
    private InventoryServiceService inventoryServiceService;

    @InjectMocks
    private InventoryServiceController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealth() {
        ResponseEntity<String> response = controller.health();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
