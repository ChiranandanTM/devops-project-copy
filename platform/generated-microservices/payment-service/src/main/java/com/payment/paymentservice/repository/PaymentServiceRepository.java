package com.payment.paymentservice.repository;

import com.payment.paymentservice.entity.PaymentServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentServiceRepository extends JpaRepository<PaymentServiceEntity, Long> {
    // Custom query methods can be added here
}
