package com.productsService.business.service;

import com.productsService.model.Category;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Optional<Category> findCategoryById(Long id);
    Category saveCategory(Category category);

    Category editCategory(Long id, Category category);

    @Transactional
    void deleteCategoryById(Long id);

    boolean isCategoryNameExisting(String name);

    boolean isCategoryPresent(Long id);
}
