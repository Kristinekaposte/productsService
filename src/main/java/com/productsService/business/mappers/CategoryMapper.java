package com.productsService.business.mappers;

import com.productsService.business.repository.model.CategoryDAO;
import com.productsService.model.Category;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface CategoryMapper {

    CategoryDAO categoryToDAO (Category category);

    Category daoToCategory (CategoryDAO categoryDAO);

}
