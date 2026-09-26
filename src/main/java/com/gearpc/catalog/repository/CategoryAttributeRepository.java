package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.CategoryAttribute;
import com.gearpc.catalog.domain.valueobject.CategoryAttributeId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            SELECT CASE WHEN COUNT(ca) > 0 THEN TRUE ELSE FALSE END
            FROM CategoryAttribute ca
            WHERE ca.category.id = :categoryId
            AND ca.required = true
            AND ca.attributeDefinition.active = true
            AND ca.attributeDefinition.deletedAt IS NULL
            AND NOT EXISTS (
                        SELECT pav.id
                        FROM ProductAttributeValue pav
                        WHERE pav.product.id = :productId
                        AND pav.attributeDefinition.id = ca.attributeDefinition.id
                        )
            """)
    boolean existsMissingRequiredAttribute(@Param("productId") UUID productId, @Param("categoryId") UUID categoryId);
}
