package com.ecommerce.ecommercewebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ecommerce.ecommercewebsite"})
public class EcommerceWebsiteApplication {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceWebsiteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EcommerceWebsiteApplication.class, args);
        logger.info("✓ EcommerceWebsite started successfully on port 8094");
    }
}
