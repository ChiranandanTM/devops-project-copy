package com.inventory.inventoryservice.service;

import com.inventory.inventoryservice.dto.InventoryServiceDTO;
import com.inventory.inventoryservice.entity.InventoryServiceEntity;
import com.inventory.inventoryservice.repository.InventoryServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceService.class);

    @Autowired
    private InventoryServiceRepository repository;

    public List<InventoryServiceDTO> getAll() {
        logger.debug("Fetching all records");
        return repository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public InventoryServiceDTO getById(Long id) {
        logger.debug("Fetching record with id: {}", id);
        return repository.findById(id)
            .map(this::convertToDTO)
            .orElse(null);
    }

    public InventoryServiceDTO create(InventoryServiceDTO dto) {
        logger.info("Creating new record");
        InventoryServiceEntity entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    public InventoryServiceDTO update(Long id, InventoryServiceDTO dto) {
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

    private InventoryServiceDTO convertToDTO(InventoryServiceEntity entity) {
        InventoryServiceDTO dto = new InventoryServiceDTO();
        return dto;
    }

    private InventoryServiceEntity convertToEntity(InventoryServiceDTO dto) {
        InventoryServiceEntity entity = new InventoryServiceEntity();
        return entity;
    }
}
