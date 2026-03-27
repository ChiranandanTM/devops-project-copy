package com.demo.demoservice.controller;

import com.demo.demoservice.dto.DemoServiceDTO;
import com.demo.demoservice.service.DemoServiceService;
import com.demo.demoservice.entity.DemoServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo-service")
@CrossOrigin(origins = "*")
public class DemoServiceController {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceController.class);

    @Autowired
    private DemoServiceService demoServiceService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("Health check endpoint called");
        return ResponseEntity.ok("DemoService is healthy");
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all records");
        return ResponseEntity.ok(demoServiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("Fetching record with id: {}", id);
        return ResponseEntity.ok(demoServiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DemoServiceDTO dto) {
        logger.info("Creating new record");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(demoServiceService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DemoServiceDTO dto) {
        logger.info("Updating record with id: {}", id);
        return ResponseEntity.ok(demoServiceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("Deleting record with id: {}", id);
        demoServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
