package com.gearpc.catalog.application.mapper;

import com.gearpc.catalog.application.dto.request.CreateCategoryRequest;
import com.gearpc.catalog.application.dto.response.CategoryOptionResponse;
import com.gearpc.catalog.application.dto.response.CreateCategoryResponse;
import com.gearpc.catalog.application.dto.response.DetailCategoryResponse;
import com.gearpc.catalog.application.dto.response.UpdateCategoryResponse;
import com.gearpc.catalog.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toCategory(CreateCategoryRequest createCategoryRequest);

    CreateCategoryResponse toCreateCategoryResponse(Category category);

    DetailCategoryResponse toDetailCategoryResponse(Category category);

    UpdateCategoryResponse toUpdateCategoryResponse(Category category);

    CategoryOptionResponse toCategoryOptionResponse(Category category);

}
