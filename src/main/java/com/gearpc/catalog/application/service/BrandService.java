package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.BrandRequest;
import com.gearpc.catalog.application.dto.BrandResponse;
import com.gearpc.common.dto.PaginationResponse;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface BrandService {

    BrandResponse createBrand(BrandRequest brandRequest);

    PaginationResponse<BrandResponse> searchBrandsForAdmin(
            @NonNull @RequestParam(required = false) String keyword,
            @NonNull @RequestParam(required = false) Boolean active,
            @PageableDefault(
                    size = 15,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );

    BrandResponse getBrand(@NonNull UUID id);

    BrandResponse updateBrand(@NonNull UUID id, BrandRequest brandRequest);

    BrandResponse updateBrandStatus(@NonNull UUID id, Boolean active);

    void deleteBrand(@NonNull UUID id);


}
