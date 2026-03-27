package com.ecommerce.ecommercewebsite.service;

import com.ecommerce.ecommercewebsite.dto.EcommerceWebsiteDTO;
import com.ecommerce.ecommercewebsite.entity.EcommerceWebsiteEntity;
import com.ecommerce.ecommercewebsite.repository.EcommerceWebsiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EcommerceWebsiteService {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceWebsiteService.class);

    @Autowired
    private EcommerceWebsiteRepository repository;

    public List<EcommerceWebsiteDTO> getAll() {
        logger.debug("Fetching all records");
        return repository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public EcommerceWebsiteDTO getById(Long id) {
        logger.debug("Fetching record with id: {}", id);
        return repository.findById(id)
            .map(this::convertToDTO)
            .orElse(null);
    }

    public EcommerceWebsiteDTO create(EcommerceWebsiteDTO dto) {
        logger.info("Creating new record");
        EcommerceWebsiteEntity entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    public EcommerceWebsiteDTO update(Long id, EcommerceWebsiteDTO dto) {
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

    private EcommerceWebsiteDTO convertToDTO(EcommerceWebsiteEntity entity) {
        EcommerceWebsiteDTO dto = new EcommerceWebsiteDTO();
        return dto;
    }

    private EcommerceWebsiteEntity convertToEntity(EcommerceWebsiteDTO dto) {
        EcommerceWebsiteEntity entity = new EcommerceWebsiteEntity();
        return entity;
    }
}
