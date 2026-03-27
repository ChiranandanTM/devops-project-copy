package com.demo.demoservice.repository;

import com.demo.demoservice.entity.DemoServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoServiceRepository extends JpaRepository<DemoServiceEntity, Long> {
    // Custom query methods can be added here
}
