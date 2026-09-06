package com.gearpc.catalog.application.service.impl;

import com.gearpc.catalog.application.dto.request.CreateCategoryRequest;
import com.gearpc.catalog.application.dto.request.UpdateCategoryRequest;
import com.gearpc.catalog.application.dto.response.*;
import com.gearpc.catalog.application.mapper.CategoryMapper;
import com.gearpc.catalog.application.service.CategoryService;
import com.gearpc.catalog.domain.entity.Category;
import com.gearpc.catalog.repository.CategoryRepository;
import com.gearpc.catalog.repository.specification.CategorySpecification;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        if(categoryRepository.existsByNameIgnoreCase(createCategoryRequest.name()))
            throw new AppException(ErrorCode.CATEGORY_EXISTS);
        Category category = categoryMapper.toCategory(createCategoryRequest);
        category.setSlug(SlugUtils.generateSlug(category.getName()));
        categoryRepository.save(category);
        return categoryMapper.toCreateCategoryResponse(category);
    }

    @Override
    public PaginationResponse<DetailCategoryResponse> searchCategoriesForAdmin(String keyword, Boolean active, Pageable pageable) {
        Specification<Category> spec = Specification.where(CategorySpecification.hasKeyword(keyword))
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
                .content(content)
                .build();
    }

    @Override
    public List<CategoryOptionResponse> getActiveCategoryOptions() {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(category -> new CategoryOptionResponse(category.getId(), category.getName()))
                .toList();
    }

    @Override
    public DetailCategoryResponse getCategory(@NonNull UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryMapper.toDetailCategoryResponse(category);
    }

    @Override
    @Transactional
    public UpdateCategoryResponse updateCategory(@NonNull UUID id, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (updateCategoryRequest.name() != null && !category.getName().equals(updateCategoryRequest.name())) {
            if (categoryRepository.existsByNameIgnoreCase(updateCategoryRequest.name())) {
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
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Optional.ofNullable(active).ifPresent(category::setActive);

        return categoryMapper.toUpdateCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(@NonNull UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // TODO: Cần kiểm tra xem Category có đang chứa Product nào không trước khi xóa
        ///  if (productRepository.existsByCategoryId(id)) {
        //     throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        // }
        categoryRepository.delete(category);
    }
}
