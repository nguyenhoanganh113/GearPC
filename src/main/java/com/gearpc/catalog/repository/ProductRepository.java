package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByName(String name);

    boolean existsBySku(String sku);

}
