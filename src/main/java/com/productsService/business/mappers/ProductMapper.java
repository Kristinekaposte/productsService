package com.productsService.business.mappers;

import com.productsService.business.repository.model.ProductDAO;
import com.productsService.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDAO productToDAO (Product product);

    Product daoToProduct (ProductDAO productDAO);

}
