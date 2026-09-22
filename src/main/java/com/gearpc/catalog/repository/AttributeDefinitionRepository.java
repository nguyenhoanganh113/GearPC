package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {
}
