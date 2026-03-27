package com.ecommerce.ecommercewebsite.controller;

import com.ecommerce.ecommercewebsite.dto.EcommerceWebsiteDTO;
import com.ecommerce.ecommercewebsite.service.EcommerceWebsiteService;
import com.ecommerce.ecommercewebsite.entity.EcommerceWebsiteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecommerce-website")
@CrossOrigin(origins = "*")
public class EcommerceWebsiteController {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceWebsiteController.class);

    @Autowired
    private EcommerceWebsiteService ecommerceWebsiteService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("Health check endpoint called");
        return ResponseEntity.ok("EcommerceWebsite is healthy");
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all records");
        return ResponseEntity.ok(ecommerceWebsiteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("Fetching record with id: {}", id);
        return ResponseEntity.ok(ecommerceWebsiteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody EcommerceWebsiteDTO dto) {
        logger.info("Creating new record");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ecommerceWebsiteService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EcommerceWebsiteDTO dto) {
        logger.info("Updating record with id: {}", id);
        return ResponseEntity.ok(ecommerceWebsiteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("Deleting record with id: {}", id);
        ecommerceWebsiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
