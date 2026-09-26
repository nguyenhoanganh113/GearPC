package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.CategoryAttribute;
import com.gearpc.catalog.domain.valueobject.CategoryAttributeId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, CategoryAttributeId> {

    @EntityGraph(attributePaths = {"category", "attributeDefinition"})
    List<CategoryAttribute> findAllByCategory_IdAndAttributeDefinition_DeletedAtIsNullOrderByAttributeDefinition_NameAsc(
            UUID categoryId
    );

    @EntityGraph(attributePaths = {"category", "attributeDefinition"})
    List<CategoryAttribute> findAllByCategory_IdAndAttributeDefinition_ActiveTrueAndAttributeDefinition_DeletedAtIsNullOrderByAttributeDefinition_NameAsc(
            UUID categoryId
    );

    @EntityGraph(attributePaths = {"category", "attributeDefinition"})
    List<CategoryAttribute> findAllByCategory_Id(UUID categoryId);

    @EntityGraph(attributePaths = {"category", "attributeDefinition"})
    Optional<CategoryAttribute> findByIdAndCategory_DeletedAtIsNullAndAttributeDefinition_DeletedAtIsNull(
            CategoryAttributeId id
    );

}
