package com.gearpc.catalog.application.mapper;

import com.gearpc.catalog.application.dto.request.CreateProductRequest;
import com.gearpc.catalog.application.dto.response.CreateProductResponse;
import com.gearpc.catalog.application.dto.response.DetailProductResponse;
import com.gearpc.catalog.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, BrandMapper.class}
)
public interface ProductMapper {

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "productStatus", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Product toProduct(CreateProductRequest createProductRequest);

    CreateProductResponse toCreateProductResponse(Product product);

    DetailProductResponse toDetailProductResponse(Product product);

}
