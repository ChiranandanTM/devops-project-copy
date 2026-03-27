package com.inventory.inventoryservice.repository;

import com.inventory.inventoryservice.entity.InventoryServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryServiceRepository extends JpaRepository<InventoryServiceEntity, Long> {
    // Custom query methods can be added here
}
