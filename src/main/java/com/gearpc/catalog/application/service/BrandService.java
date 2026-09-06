package com.gearpc.catalog.application.service;

import com.gearpc.catalog.application.dto.request.UpdateBrandRequest;
import com.gearpc.catalog.application.dto.request.CreateBrandRequest;
import com.gearpc.catalog.application.dto.response.BrandOptionResponse;
import com.gearpc.catalog.application.dto.response.CreateBrandResponse;
import com.gearpc.catalog.application.dto.response.DetailBrandResponse;
import com.gearpc.catalog.application.dto.response.UpdateBrandResponse;
import com.gearpc.common.dto.PaginationResponse;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface BrandService {

    CreateBrandResponse createBrand(CreateBrandRequest brandRequest);

    PaginationResponse<DetailBrandResponse> searchBrandsForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    );

    List<BrandOptionResponse> getActiveBrandOptions();

    DetailBrandResponse getBrand(@NonNull UUID id);

    UpdateBrandResponse updateBrand(@NonNull UUID id, UpdateBrandRequest brandRequest);

    UpdateBrandResponse updateBrandStatus(@NonNull UUID id, Boolean active);

    void deleteBrand(@NonNull UUID id);

}
