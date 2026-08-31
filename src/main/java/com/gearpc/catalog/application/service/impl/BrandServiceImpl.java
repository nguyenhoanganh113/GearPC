package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateBrandRequest;
import com.gearpc.catalog.application.dto.response.BrandOptionResponse;
import com.gearpc.catalog.application.dto.response.CreateBrandResponse;
import com.gearpc.catalog.application.dto.response.DetailBrandResponse;
import com.gearpc.catalog.application.dto.request.UpdateBrandRequest;
import com.gearpc.catalog.application.dto.response.UpdateBrandResponse;
import com.gearpc.catalog.application.mapper.BrandMapper;
import com.gearpc.catalog.application.service.BrandService;
import com.gearpc.catalog.domain.entity.Brand;
import com.gearpc.catalog.repository.BrandRepository;
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

    @Override
    public CreateBrandResponse createBrand(CreateBrandRequest brandRequest) {

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
    public PaginationResponse<DetailBrandResponse> searchBrandsForAdmin(String keyword, Boolean active, Pageable pageable) {
        Specification<Brand> spec = Specification.where(BrandSpecification.hasKeyword(keyword))
                .and(BrandSpecification.isActive(active));
        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);
        // Vấn đề: Convert Page<Brand> sang PaginationResponse<DetailBrandResponse>
        List<DetailBrandResponse> content = brandPage.getContent().stream()
                .map(brandMapper::toDetailBrandResponse)
                .toList();
        return new PaginationResponse<>(
                content,
                brandPage.getNumber(),
                brandPage.getSize(),
                brandPage.getTotalElements(),
                brandPage.getTotalPages(),
                brandPage.isFirst(),
                brandPage.isLast()
        );
    }

    @Override
    public List<BrandOptionResponse> getActiveBrandOptions() {
        return brandRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(brand -> new BrandOptionResponse(brand.getId(), brand.getName()))
                .toList();
    }

    @Override
    public DetailBrandResponse getBrand(@NonNull UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toDetailBrandResponse(brand);
    }

    @Override
    @Transactional
    public UpdateBrandResponse updateBrand(@NonNull UUID id, UpdateBrandRequest brandRequest) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        if (brandRequest.name() != null && !brand.getName().equals(brandRequest.name())) {
            if (brandRepository.existsByName(brandRequest.name())) {
                throw new AppException(ErrorCode.BRAND_EXISTS);
            }
            // Cập nhật name và đồng thời tự động sinh lại slug mới tương ứng
            brand.setName(brandRequest.name());
            brand.setSlug(SlugUtils.generateSlug(brandRequest.name()));
        }
        Optional.ofNullable(brandRequest.logoUrl()).ifPresent(brand::setLogoUrl);
        return brandMapper.toUpdateBrandResponse(brand);
    }

    @Override
    @Transactional
    public UpdateBrandResponse updateBrandStatus(@NonNull UUID id, Boolean active) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        Optional.ofNullable(active).ifPresent(brand::setActive);
        return brandMapper.toUpdateBrandResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(@NonNull UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        brandRepository.delete(brand);
    }
}
