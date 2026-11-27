package com.gearpc.service.impl;

import com.gearpc.dto.request.CreateCategoryRequest;
import com.gearpc.dto.request.UpdateCategoryRequest;
import com.gearpc.dto.response.*;
import com.gearpc.mapper.CategoryMapper;
import com.gearpc.service.CategoryService;
import com.gearpc.entity.Category;
import com.gearpc.entity.valueobject.enums.ProductStatus;
import com.gearpc.repository.CategoryRepository;
import com.gearpc.repository.ProductRepository;
import com.gearpc.repository.specification.CategorySpecification;
import com.gearpc.dto.PaginationResponse;
import com.gearpc.exception.AppException;
import com.gearpc.exception.ErrorCode;
import com.gearpc.util.SlugUtils;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        if(categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(createCategoryRequest.name()))
            throw new AppException(ErrorCode.CATEGORY_EXISTS);
        Category category = categoryMapper.toCategory(createCategoryRequest);
        category.setSlug(SlugUtils.generateSlug(category.getName()));
        categoryRepository.save(category);
        return categoryMapper.toCreateCategoryResponse(category);
    }

    @Override
    public PaginationResponse<DetailCategoryResponse> searchCategoriesForAdmin(String keyword, Boolean active, Pageable pageable) {
        Specification<Category> spec =
                Specification.where(CategorySpecification.isNotDeleted())
                                        .and(CategorySpecification.hasKeyword(keyword))
                                        .and(CategorySpecification.isActive(active));

        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        List<DetailCategoryResponse> content = categoryPage.getContent().stream()
                .map(categoryMapper::toDetailCategoryResponse)
                .toList();

        return PaginationResponse.<DetailCategoryResponse>builder()
                .pageNo(categoryPage.getNumber() + 1) // Page number is 0-based in Spring Data, so we add 1 for 1-based page number
                .pageSize(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .first(categoryPage.isFirst())
                .last(categoryPage.isLast())
                .content(content)
                .build();
    }

    @Override
    public List<CategoryOptionResponse> getActiveCategoryOptions() {
        return categoryRepository.findAllByActiveTrueAndDeletedAtIsNullOrderByNameAsc()
                .stream()
                .map(category -> new CategoryOptionResponse(category.getId(), category.getName()))
                .toList();
    }

    @Override
    public DetailCategoryResponse getCategory(@NonNull UUID id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryMapper.toDetailCategoryResponse(category);
    }

    @Override
    @Transactional
    public UpdateCategoryResponse updateCategory(@NonNull UUID id, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (updateCategoryRequest.name() != null && !category.getName().equals(updateCategoryRequest.name())) {
            if (categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(updateCategoryRequest.name(), id)) {
                throw new AppException(ErrorCode.CATEGORY_EXISTS);
            }
            category.setName(updateCategoryRequest.name());
            category.setSlug(SlugUtils.generateSlug(updateCategoryRequest.name()));
        }

        Optional.ofNullable(updateCategoryRequest.description()).ifPresent(category::setDescription);
        Optional.ofNullable(updateCategoryRequest.imageUrl()).ifPresent(category::setImageUrl);

        return categoryMapper.toUpdateCategoryResponse(category);
    }

    @Override
    @Transactional
    public UpdateCategoryResponse updateCategoryStatus(@NonNull UUID id, Boolean active) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (Boolean.FALSE.equals(active)
                && category.isActive()
                && productRepository
                .existsByCategory_IdAndProductStatusAndDeletedAtIsNull(
                        id,
                        ProductStatus.ACTIVE
                )) {
            throw new AppException(ErrorCode.CATEGORY_HAS_ACTIVE_PRODUCTS);
        }

        Optional.ofNullable(active).ifPresent(category::setActive);

        return categoryMapper.toUpdateCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(@NonNull UUID id) {
        Category category = categoryRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (productRepository.existsByCategory_IdAndDeletedAtIsNull(id)) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }

        category.softDelete();
    }
}
