package com.gearpc.catalog.controller;

import com.gearpc.catalog.application.dto.BrandRequest;
import com.gearpc.catalog.application.dto.BrandResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/brands")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BrandResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo thương hiệu thành công")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<BrandResponse>>> searchBrandsForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(
                    size = 15,
                    sort = {"createdAt", "id"}, // tier-breaker
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PaginationResponse<BrandResponse> brands = brandService.searchBrandsForAdmin(keyword, active, pageable);
        return ResponseEntity.ok(
                ApiResponse.<PaginationResponse<BrandResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Tìm kiếm danh sách thương hiệu thành công")
                        .data(brands)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@NonNull @PathVariable UUID id) {
        BrandResponse response = brandService.getBrand(id);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Lấy chi tiết thương hiệu thành công")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @NonNull @PathVariable UUID id,
            @Valid @RequestBody BrandRequest request
    ) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật thương hiệu thành công")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BrandResponse>> toggleBrandStatus(
            @NonNull @PathVariable UUID id,
            @RequestParam boolean active
    ) {
        BrandResponse response = brandService.updateBrandStatus(id, active);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Cập nhật trạng thái thương hiệu thành công")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@NonNull @PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Xóa thương hiệu thành công")
                        .build()
        );
    }
}
