package com.ecommerce.ecommercewebsite;

import com.ecommerce.ecommercewebsite.service.EcommerceWebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EcommerceWebsiteServiceTest {

    private EcommerceWebsiteService service;

    @BeforeEach
    void setUp() {
        service = new EcommerceWebsiteService();
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(service);
    }
}
