package com.inventory.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {"com.inventory.inventoryservice"})
public class InventoryServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
        logger.info("✓ InventoryService started successfully on port 8093");
    }
}
