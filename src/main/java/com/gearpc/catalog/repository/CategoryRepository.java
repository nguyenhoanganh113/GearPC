package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    List<Category> findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc();

    Optional<Category> findByIdAndDeletedAtIsNull(UUID id);

}
