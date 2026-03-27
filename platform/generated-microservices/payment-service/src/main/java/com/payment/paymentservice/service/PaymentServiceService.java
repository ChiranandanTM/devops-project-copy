package com.payment.paymentservice.service;

import com.payment.paymentservice.dto.PaymentServiceDTO;
import com.payment.paymentservice.entity.PaymentServiceEntity;
import com.payment.paymentservice.repository.PaymentServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceService.class);

    @Autowired
    private PaymentServiceRepository repository;

    public List<PaymentServiceDTO> getAll() {
        logger.debug("Fetching all records");
        return repository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public PaymentServiceDTO getById(Long id) {
        logger.debug("Fetching record with id: {}", id);
        return repository.findById(id)
            .map(this::convertToDTO)
            .orElse(null);
    }

    public PaymentServiceDTO create(PaymentServiceDTO dto) {
        logger.info("Creating new record");
        PaymentServiceEntity entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    public PaymentServiceDTO update(Long id, PaymentServiceDTO dto) {
        logger.info("Updating record with id: {}", id);
        var entity = repository.findById(id).orElse(null);
        if (entity != null) {
            repository.save(entity);
        }
        return convertToDTO(entity);
    }

    public void delete(Long id) {
        logger.info("Deleting record with id: {}", id);
        repository.deleteById(id);
    }

    private PaymentServiceDTO convertToDTO(PaymentServiceEntity entity) {
        PaymentServiceDTO dto = new PaymentServiceDTO();
        return dto;
    }

    private PaymentServiceEntity convertToEntity(PaymentServiceDTO dto) {
        PaymentServiceEntity entity = new PaymentServiceEntity();
        return entity;
    }
}
