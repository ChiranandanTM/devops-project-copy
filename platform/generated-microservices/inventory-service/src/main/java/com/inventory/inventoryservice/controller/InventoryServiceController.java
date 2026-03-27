package com.inventory.inventoryservice.controller;

import com.inventory.inventoryservice.dto.InventoryServiceDTO;
import com.inventory.inventoryservice.service.InventoryServiceService;
import com.inventory.inventoryservice.entity.InventoryServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory-service")
@CrossOrigin(origins = "*")
public class InventoryServiceController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceController.class);

    @Autowired
    private InventoryServiceService inventoryServiceService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("Health check endpoint called");
        return ResponseEntity.ok("InventoryService is healthy");
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all records");
        return ResponseEntity.ok(inventoryServiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("Fetching record with id: {}", id);
        return ResponseEntity.ok(inventoryServiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InventoryServiceDTO dto) {
        logger.info("Creating new record");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(inventoryServiceService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InventoryServiceDTO dto) {
        logger.info("Updating record with id: {}", id);
        return ResponseEntity.ok(inventoryServiceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("Deleting record with id: {}", id);
        inventoryServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
