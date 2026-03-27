package com.demo.demoservice.service;

import com.demo.demoservice.dto.DemoServiceDTO;
import com.demo.demoservice.entity.DemoServiceEntity;
import com.demo.demoservice.repository.DemoServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DemoServiceService {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceService.class);

    @Autowired
    private DemoServiceRepository repository;

    public List<DemoServiceDTO> getAll() {
        logger.debug("Fetching all records");
        return repository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public DemoServiceDTO getById(Long id) {
        logger.debug("Fetching record with id: {}", id);
        return repository.findById(id)
            .map(this::convertToDTO)
            .orElse(null);
    }

    public DemoServiceDTO create(DemoServiceDTO dto) {
        logger.info("Creating new record");
        DemoServiceEntity entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    public DemoServiceDTO update(Long id, DemoServiceDTO dto) {
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

    private DemoServiceDTO convertToDTO(DemoServiceEntity entity) {
        DemoServiceDTO dto = new DemoServiceDTO();
        return dto;
    }

    private DemoServiceEntity convertToEntity(DemoServiceDTO dto) {
        DemoServiceEntity entity = new DemoServiceEntity();
        return entity;
    }
}
