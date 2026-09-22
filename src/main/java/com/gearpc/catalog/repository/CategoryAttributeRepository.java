package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.CategoryAttribute;
import com.gearpc.catalog.domain.valueobject.CategoryAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, CategoryAttributeId> {
}
