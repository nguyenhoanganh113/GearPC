package com.gearpc.catalog.application.mapper;

import com.gearpc.catalog.application.dto.request.CreateBrandRequest;
import com.gearpc.catalog.application.dto.response.BrandOptionResponse;
import com.gearpc.catalog.application.dto.response.CreateBrandResponse;
import com.gearpc.catalog.application.dto.response.DetailBrandResponse;
import com.gearpc.catalog.application.dto.response.UpdateBrandResponse;
import com.gearpc.catalog.domain.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandMapper {

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "products", ignore = true)
    Brand toBrand(CreateBrandRequest brandRequest);

    CreateBrandResponse toCreateBrandResponse(Brand brand);

    DetailBrandResponse toDetailBrandResponse(Brand brand);

    UpdateBrandResponse toUpdateBrandResponse(Brand brand);

    BrandOptionResponse toBrandOptionResponse(Brand brand);

}
