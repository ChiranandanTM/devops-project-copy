package com.ecommerce.ecommercewebsite;

import com.ecommerce.ecommercewebsite.controller.EcommerceWebsiteController;
import com.ecommerce.ecommercewebsite.service.EcommerceWebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EcommerceWebsiteControllerTest {

    @Mock
    private EcommerceWebsiteService ecommerceWebsiteService;

    @InjectMocks
    private EcommerceWebsiteController controller;

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
