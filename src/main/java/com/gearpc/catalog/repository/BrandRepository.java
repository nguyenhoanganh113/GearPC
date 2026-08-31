package com.gearpc.catalog.repository;

import com.gearpc.catalog.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Brand> findAllByActiveTrueOrderByNameAsc();

}
