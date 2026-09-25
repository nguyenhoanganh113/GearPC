package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID>,
        JpaSpecificationExecutor<AttributeDefinition> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<AttributeDefinition> findByIdAndDeletedAtIsNull(UUID attributeId);

    Optional<AttributeDefinition> findByIdAndActiveTrueAndDeletedAtIsNull(UUID attributeId);

    List<AttributeDefinition> findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc();
}
