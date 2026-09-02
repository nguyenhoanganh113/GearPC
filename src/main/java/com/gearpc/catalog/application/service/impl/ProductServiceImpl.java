package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.request.ProductSearchRequest;
import com.gearpc.catalog.application.dto.request.UpdateProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
import com.gearpc.catalog.application.dto.response.UpdateProductResponse;
import com.gearpc.catalog.application.mapper.ProductMapper;
import com.gearpc.catalog.application.service.ProductService;
import com.gearpc.catalog.domain.entity.Brand;
import com.gearpc.catalog.domain.entity.Category;
import com.gearpc.catalog.domain.entity.Product;
import com.gearpc.catalog.domain.valueobject.enums.ProductStatus;
import com.gearpc.catalog.repository.BrandRepository;
import com.gearpc.catalog.repository.CategoryRepository;
import com.gearpc.catalog.repository.ProductRepository;
import com.gearpc.catalog.repository.specification.ProductSpecification;
import com.gearpc.common.dto.PaginationResponse;
import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.common.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    public CreateProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsBySku(request.sku()) || productRepository.existsByName(request.name())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTS);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        Product product = productMapper.toProduct(request);
        product.setSlug(SlugUtils.generateSlug(request.name()));
        product.setCategory(category);
        product.setBrand(brand);
        product.setProductStatus(ProductStatus.INACTIVE);

        productRepository.save(product);

        return productMapper.toCreateProductResponse(product);
    }

    @Override
    public DetailProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toDetailProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<DetailProductResponse> searchProductsForAdmin(
            ProductSearchRequest productSearchRequest,
            Pageable pageable
    ) {

        // 1. Xác thực phạm vi giá hợp lệ
        if (productSearchRequest.minPrice() != null
                && productSearchRequest.maxPrice() != null
                && productSearchRequest.minPrice().compareTo(productSearchRequest.maxPrice()) > 0) {
            throw new AppException(ErrorCode.INVALID_PRICE_RANGE);
        }

        // 2. Xây dựng Sort dựa trên yêu cầu tìm kiếm và sắp xếp mặc định
        /*
        Sort sort = buildSort(productSearchRequest, pageable.getSort().and(Sort.by("id").descending()));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        **/
        Sort sort = buildSort(productSearchRequest, pageable.getSort().and(Sort.by("price").descending()));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 3. Tạo Specification để filter (kết hợp các điều kiện)
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.hasKeyword(productSearchRequest.keyword()),
                ProductSpecification.hasCategory(productSearchRequest.categoryId()),
                ProductSpecification.hasBrand(productSearchRequest.brandId()),
                ProductSpecification.hasStatus(productSearchRequest.productStatus()),
                ProductSpecification.hasPrice(productSearchRequest.minPrice(), productSearchRequest.maxPrice()),
                ProductSpecification.inStock(productSearchRequest.inStock())
        );

        // 4. Query với Specification + Pageable
        // JPA tự động: filter, paginate, sort, và count total
        Page<Product> productPage = productRepository.findAll(spec, pageRequest);

        // 5. Lấy content (danh sách products của trang hiện tại)
        List<Product> productPageContent = productPage.getContent();

        // 6. Map danh sách Product sang DetailProductResponse
        List<DetailProductResponse> detailProductResponses = productPageContent.stream()
                .map(productMapper::toDetailProductResponse)
                .toList();

        // 7. Build PageResponse với pagination metadata
        return PaginationResponse.<DetailProductResponse>builder()
                .pageNo(productPage.getNumber() + 1) // Page number is 0-based in Spring Data, so we add 1 for 1-based page number
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .content(detailProductResponses)
                .build();
    }

    @Override
    @Transactional
    public UpdateProductResponse updateProduct(UUID id, UpdateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.name() != null && !request.name().equals(product.getName())) {
            if (productRepository.existsByName(request.name())) {
                throw new AppException(ErrorCode.PRODUCT_EXISTS);
            }
            product.setName(request.name());
            product.setSlug(SlugUtils.generateSlug(request.name()));
        }

        if (request.sku() != null && !request.sku().equals(product.getSku())) {
            if (productRepository.existsBySku(request.sku())) {
                throw new AppException(ErrorCode.PRODUCT_EXISTS);
            }
            product.setSku(request.sku());
        }

        Optional.ofNullable(request.description()).ifPresent(product::setDescription);
        Optional.ofNullable(request.price()).ifPresent(product::setPrice);
        Optional.ofNullable(request.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(request.images()).ifPresent(product::setImages);
        Optional.ofNullable(request.categoryId()).ifPresent(categoryId -> {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        });
        Optional.ofNullable(request.brandId()).ifPresent(brandId -> {
            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
            product.setBrand(brand);
        });

        return productMapper.toUpdateProductResponse(product);
    }

    private Sort buildSort(ProductSearchRequest productSearchRequest, Sort defaultSort) {
        if (productSearchRequest.sortBy() == null) {
            return defaultSort;
        }

        return switch (productSearchRequest.sortBy()) {
            case PRICE_ASC -> Sort.by("price").ascending();
            case PRICE_DESC -> Sort.by("price").descending();
            case NAME_ASC -> Sort.by("name").ascending();
            case NAME_DESC -> Sort.by("name").descending();
            case CREATED_AT_ASC -> Sort.by("createdAt").ascending();
            case CREATED_AT_DESC -> Sort.by("createdAt").descending();
        };
    }
}
