package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<Brand> findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc();

    Optional<Brand> findByIdAndActiveTrueAndDeletedAtIsNull(UUID id);

    Optional<Brand> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Brand> findByIdAndDeletedAtIsNotNull(UUID id);

}
