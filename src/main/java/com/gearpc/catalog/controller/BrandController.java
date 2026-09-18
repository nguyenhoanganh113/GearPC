package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.request.CreateBrandRequest;
import com.gearpc.catalog.application.dto.request.UpdateBrandRequest;
import com.gearpc.catalog.application.dto.response.BrandOptionResponse;
import com.gearpc.catalog.application.dto.response.CreateBrandResponse;
import com.gearpc.catalog.application.dto.response.DetailBrandResponse;
import com.gearpc.catalog.application.dto.response.UpdateBrandResponse;
import com.gearpc.catalog.application.service.BrandService;
import com.gearpc.common.dto.ApiResponse;
import com.gearpc.common.dto.PaginationResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/brands")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateBrandResponse> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        CreateBrandResponse response = brandService.createBrand(request);
        return ApiResponse.created(response);
    }

    @GetMapping("/search")
    public ApiResponse<PaginationResponse<DetailBrandResponse>> searchBrandsForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(
                    size = 15,
                    sort = {"createdAt", "id"}, // tier-breaker
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PaginationResponse<DetailBrandResponse> brands = brandService.searchBrandsForAdmin(keyword, active, pageable);
        return ApiResponse.success(brands);
    }

    @GetMapping("/options")
    public ApiResponse<List<BrandOptionResponse>> getActiveBrandOptions() {
        List<BrandOptionResponse> options = brandService.getActiveBrandOptions();
        return ApiResponse.success(options);
    }

    @GetMapping("/{id}")
    public ApiResponse<DetailBrandResponse> getBrandById(@NonNull @PathVariable UUID id) {
        DetailBrandResponse response = brandService.getBrand(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateBrandResponse> updateBrand(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody UpdateBrandRequest request
    ) {
        UpdateBrandResponse response = brandService.updateBrand(id, request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UpdateBrandResponse> toggleBrandStatus(
            @NonNull @PathVariable UUID id,
            @RequestParam Boolean active
    ) {
        UpdateBrandResponse response = brandService.updateBrandStatus(id, active);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteBrand(@NonNull @PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ApiResponse.success();
    }
}
