package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.request.SearchProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public PaginationResponse<DetailProductResponse> searchProductsForAdmin(SearchProductRequest request, Pageable pageable) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.hasKeyword(request.keyword()),
                ProductSpecification.hasCategory(request.categoryId()),
                ProductSpecification.hasBrand(request.brandId()),
                ProductSpecification.hasStatus(request.productStatus()),
                ProductSpecification.hasPrice(request.minPrice(), request.maxPrice()),
                ProductSpecification.inStock(request.inStock())
        );

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<DetailProductResponse> detailProductResponses = productPage.getContent().stream()
                .map(productMapper::toDetailProductResponse)
                .toList();

        return new PaginationResponse<>(
                detailProductResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }
}
