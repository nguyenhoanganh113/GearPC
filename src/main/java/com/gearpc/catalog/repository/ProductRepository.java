package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    boolean existsByName(String name);

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndDeletedAtIsNullAndCategory_DeletedAtIsNull(UUID id);

}
