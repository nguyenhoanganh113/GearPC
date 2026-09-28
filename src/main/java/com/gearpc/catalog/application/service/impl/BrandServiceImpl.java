package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateBrandRequest;
import com.gearpc.catalog.application.dto.response.*;
import com.gearpc.catalog.application.dto.request.UpdateBrandRequest;
import com.gearpc.catalog.application.mapper.BrandMapper;
import com.gearpc.catalog.application.service.BrandService;
import com.gearpc.catalog.domain.entity.Brand;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.catalog.repository.BrandRepository;
import com.gearpc.catalog.repository.ProductRepository;
import com.gearpc.catalog.repository.specification.BrandSpecification;
import com.gearpc.common.dto.PaginationResponse;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.common.util.SlugUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductRepository productRepository;

    @Override
    public CreateBrandResponse createBrand(CreateBrandRequest brandRequest) {

        if (brandRepository.existsByNameIgnoreCase(brandRequest.name())) {
            throw new AppException(ErrorCode.BRAND_EXISTS);
        }

        Brand brand = brandMapper.toBrand(brandRequest);

        String slug = SlugUtils.generateSlug(brandRequest.name());
        if (brandRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.BRAND_EXISTS);
        }
        brand.setSlug(slug);

        brandRepository.save(brand);

        return brandMapper.toCreateBrandResponse(brand);
    }

    @Override
    public PaginationResponse<DetailBrandResponse> searchBrandsForAdmin(
            String keyword,
            Boolean active,
            Boolean deleted,
            Pageable pageable
    ) {
        Specification<Brand> brandSpecification = Specification.allOf(
                BrandSpecification.hasKeyword(keyword),
                BrandSpecification.isActive(active),
                BrandSpecification.isDeleted(deleted)
        );

        Page<Brand> brandPage = brandRepository.findAll(brandSpecification, pageable);

        // Vấn đề: Convert Page<Brand> sang PaginationResponse<DetailBrandResponse>
        List<DetailBrandResponse> content = brandPage.getContent().stream()
                .map(brandMapper::toDetailBrandResponse)
                .toList();

        return PaginationResponse.<DetailBrandResponse>builder()
                .pageNo(brandPage.getNumber() + 1) // Page number is 0-based in Spring Data, so we add 1 for 1-based page number
                .pageSize(brandPage.getSize())
                .totalPages(brandPage.getTotalPages())
                .totalElements(brandPage.getTotalElements())
                .content(content)
                .build();
    }

    @Override
    public List<BrandOptionResponse> getActiveBrandOptions() {
        return brandRepository.findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc()
                .stream()
                .map(brand -> new BrandOptionResponse(brand.getId(), brand.getName(), brand.getSlug()))
                .toList();
    }

    @Override
    public DetailBrandResponse getBrand(@NonNull UUID id) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toDetailBrandResponse(brand);
    }

    @Override
    @Transactional
    public UpdateBrandResponse updateBrand(@NonNull UUID id, UpdateBrandRequest brandRequest) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        if (brandRequest.name() != null && !brandRequest.name().equalsIgnoreCase(brand.getName())) {
            if (brandRepository.existsByNameIgnoreCaseAndIdNot(
                    brandRequest.name(),
                    id
            )) {
                throw new AppException(ErrorCode.BRAND_EXISTS);
            }

            String newSlug = SlugUtils.generateSlug(brandRequest.name());

            if (brandRepository.existsBySlugAndIdNot(newSlug, id)) {
                throw new AppException(ErrorCode.BRAND_EXISTS);
            }

            brand.setName(brandRequest.name());
            brand.setSlug(newSlug);
        }

        Optional.ofNullable(brandRequest.logoUrl()).ifPresent(brand::setLogoUrl);
        return brandMapper.toUpdateBrandResponse(brand);
    }

    @Override
    @Transactional
    public UpdateBrandResponse updateBrandStatus(@NonNull UUID id, Boolean active) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        if (Boolean.FALSE.equals(active)
                && brand.isActive()
                && productRepository
                .existsByBrand_IdAndProductStatusAndDeletedAtIsNull(
                        id,
                        ProductStatus.ACTIVE
                )) {
            throw new AppException(ErrorCode.BRAND_HAS_ACTIVE_PRODUCTS);
        }

        Optional.ofNullable(active).ifPresent(brand::setActive);
        return brandMapper.toUpdateBrandResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(@NonNull UUID id) {
        Brand brand = brandRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        if (productRepository.existsByBrand_IdAndDeletedAtIsNull(id)) {
            throw new AppException(ErrorCode.BRAND_IN_USE);
        }
        brand.softDelete();
    }

    @Override
    @Transactional
    public UpdateBrandResponse restoreBrand(@NonNull UUID id) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        brand.restore();
        brand.setActive(false);

        return brandMapper.toUpdateBrandResponse(brand);
    }
}
