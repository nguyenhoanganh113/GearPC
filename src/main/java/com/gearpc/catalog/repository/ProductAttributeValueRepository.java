package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.ProductAttributeValue;
import com.gearpc.catalog.domain.valueobject.ProductAttributeValueId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductAttributeValueRepository
        extends JpaRepository<ProductAttributeValue, ProductAttributeValueId> {

    @EntityGraph(attributePaths = {"product", "attributeDefinition"})
    List<ProductAttributeValue> findAllByProduct_IdOrderByAttributeDefinition_NameAsc(UUID productId);

    boolean existsByProduct_Category_IdAndAttributeDefinition_Id(UUID categoryId, UUID attributeDefinitionId);

    boolean existByProduct_Id(UUID productId);
}
