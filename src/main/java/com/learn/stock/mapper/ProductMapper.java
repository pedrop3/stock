package com.learn.stock.mapper;

import com.learn.stock.model.Product;
import com.learn.stock.response.ProductDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<ProductDTO> toDtoList(List<Product> products);
    Map<String, List<ProductDTO>> toDtoMapList( Map<String, List<Product>> productMapList);
    ProductDTO toDto(Product product);
}

