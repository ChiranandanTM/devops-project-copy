package com.payment.paymentservice;

import com.payment.paymentservice.controller.PaymentServiceController;
import com.payment.paymentservice.service.PaymentServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceControllerTest {

    @Mock
    private PaymentServiceService paymentServiceService;

    @InjectMocks
    private PaymentServiceController controller;

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
