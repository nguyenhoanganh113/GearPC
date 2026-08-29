package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.BrandRequest;
import com.gearpc.catalog.application.dto.BrandResponse;
import com.gearpc.catalog.application.service.BrandService;
import com.gearpc.catalog.domain.entity.Brand;
import com.gearpc.catalog.repository.BrandRepository;
import com.gearpc.common.dto.PaginationResponse;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.common.util.SlugUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest) {

        String slug = SlugUtils.generateSlug(brandRequest.name());

        if (brandRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.BRAND_EXISTS);
        }

        Brand brand = Brand.builder()
                .name(brandRequest.name())
                .logoUrl(brandRequest.logoUrl())
                .slug(slug)
                .build();

        brandRepository.save(brand);

        return BrandResponse.builder()
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .active(brand.isActive())
                .createdAt(brand.getCreatedAt())
                .lastModifiedAt(brand.getLastModifiedAt())
                .build();
    }

    @Override
    public PaginationResponse<BrandResponse> searchBrandsForAdmin(@NonNull String keyword, @NonNull Boolean active, Pageable pageable) {
        return null;
    }

    @Override
    public BrandResponse getBrand(@NonNull UUID id) {
        return null;
    }

    @Override
    public BrandResponse updateBrand(@NonNull UUID id, BrandRequest brandRequest) {
        return null;
    }

    @Override
    public BrandResponse updateBrandStatus(@NonNull UUID id, Boolean active) {
        return null;
    }

    @Override
    public void deleteBrand(@NonNull UUID id) {

    }
}
