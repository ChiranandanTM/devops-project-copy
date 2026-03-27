package com.payment.paymentservice.controller;

import com.payment.paymentservice.dto.PaymentServiceDTO;
import com.payment.paymentservice.service.PaymentServiceService;
import com.payment.paymentservice.entity.PaymentServiceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-service")
@CrossOrigin(origins = "*")
public class PaymentServiceController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceController.class);

    @Autowired
    private PaymentServiceService paymentServiceService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("Health check endpoint called");
        return ResponseEntity.ok("PaymentService is healthy");
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        logger.info("Fetching all records");
        return ResponseEntity.ok(paymentServiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("Fetching record with id: {}", id);
        return ResponseEntity.ok(paymentServiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentServiceDTO dto) {
        logger.info("Creating new record");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(paymentServiceService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PaymentServiceDTO dto) {
        logger.info("Updating record with id: {}", id);
        return ResponseEntity.ok(paymentServiceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("Deleting record with id: {}", id);
        paymentServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
