package com.ecommerce.ecommercewebsite.repository;

import com.ecommerce.ecommercewebsite.entity.EcommerceWebsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcommerceWebsiteRepository extends JpaRepository<EcommerceWebsiteEntity, Long> {
    // Custom query methods can be added here
}
