package com.payment.paymentservice;

import com.payment.paymentservice.service.PaymentServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceServiceTest {

    private PaymentServiceService service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceService();
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(service);
    }
}
