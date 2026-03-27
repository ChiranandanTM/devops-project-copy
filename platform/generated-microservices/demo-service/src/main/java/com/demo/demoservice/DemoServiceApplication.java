package com.demo.demoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {"com.demo.demoservice"})
public class DemoServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoServiceApplication.class, args);
        logger.info("✓ DemoService started successfully on port 8091");
    }
}
