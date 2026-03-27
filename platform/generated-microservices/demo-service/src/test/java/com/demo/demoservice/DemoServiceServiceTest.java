package com.demo.demoservice;

import com.demo.demoservice.service.DemoServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DemoServiceServiceTest {

    private DemoServiceService service;

    @BeforeEach
    void setUp() {
        service = new DemoServiceService();
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(service);
    }
}
