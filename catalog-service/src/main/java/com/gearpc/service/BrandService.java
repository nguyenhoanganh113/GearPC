package com.gearpc.service;

import com.gearpc.dto.request.UpdateBrandRequest;
import com.gearpc.dto.request.CreateBrandRequest;
import com.gearpc.dto.response.BrandOptionResponse;
import com.gearpc.dto.response.CreateBrandResponse;
import com.gearpc.dto.response.DetailBrandResponse;
import com.gearpc.dto.response.UpdateBrandResponse;
import com.gearpc.dto.PaginationResponse;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BrandService {

    CreateBrandResponse createBrand(CreateBrandRequest brandRequest);

    PaginationResponse<DetailBrandResponse> searchBrandsForAdmin(
            String keyword,
            Boolean active,
            Boolean deleted,
            Pageable pageable
    );

    List<BrandOptionResponse> getActiveBrandOptions();

    DetailBrandResponse getBrand(@NonNull UUID id);

    UpdateBrandResponse updateBrand(@NonNull UUID id, UpdateBrandRequest brandRequest);

    UpdateBrandResponse updateBrandStatus(@NonNull UUID id, Boolean active);

    void deleteBrand(@NonNull UUID id);

    UpdateBrandResponse restoreBrand(@NonNull UUID id);

}
