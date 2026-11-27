package com.gearpc.mapper;

import com.gearpc.dto.request.CreateBrandRequest;
import com.gearpc.dto.response.BrandOptionResponse;
import com.gearpc.dto.response.CreateBrandResponse;
import com.gearpc.dto.response.DetailBrandResponse;
import com.gearpc.dto.response.UpdateBrandResponse;
import com.gearpc.entity.Brand;
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
