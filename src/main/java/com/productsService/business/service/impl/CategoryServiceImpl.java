package com.productsService.business.service.impl;

import com.productsService.business.mappers.CategoryMapper;
import com.productsService.business.repository.CategoryRepository;
import com.productsService.business.repository.ProductRepository;
import com.productsService.business.repository.model.CategoryDAO;
import com.productsService.business.repository.model.ProductDAO;
import com.productsService.business.service.CategoryService;
import com.productsService.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> list = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::daoToCategory)
                .collect(Collectors.toList());
        log.info("Size of the Category list: {}", list.size());
        return list;
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        Optional<CategoryDAO> categoryDAO = categoryRepository.findById(id);
        if (!categoryDAO.isPresent()) {
            log.info("Category with id {} does not exist.", id);
            return Optional.empty();
        }
        log.info("Category with id {} found.", id);
        return categoryDAO.map(categoryMapper::daoToCategory);
    }

    @Override
    public Category saveCategory(Category category) {
        log.info("Saving Category entry: {}", category);
        return categoryMapper.daoToCategory(categoryRepository.save(categoryMapper.categoryToDAO(category)));
    }

    /**
     * Finds the existing id in database
     * Using BeanUtils Updates the existing CategoryDAO using the data from Category
     * saves the updated category
     * returns the mapped categoryDAO back to category
     * if category not found it returns null
     */
    @Override
    public Category editCategory(Long id, Category UpdatedCategory) {
        Optional<CategoryDAO> optionalCategoryDAO = categoryRepository.findById(id);
        if (optionalCategoryDAO.isPresent()) {
            CategoryDAO existingCategoryDAO = optionalCategoryDAO.get();
            BeanUtils.copyProperties(UpdatedCategory, existingCategoryDAO, "id");

            Category updatedCategoryObject = categoryMapper.daoToCategory(categoryRepository.save(existingCategoryDAO));
            log.info("Category entry with ID: {} updated", id);
            return updatedCategoryObject;
        } else
            log.warn("Failed to update category. Category entry with ID: {} not found", id);
        return null;
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long id) {
        Optional<CategoryDAO> optionalCategoryDAO = categoryRepository.findById(id);
        CategoryDAO category = optionalCategoryDAO.get();
        List<ProductDAO> products = category.getProducts();
        productRepository.deleteAll(products);
        categoryRepository.delete(category);
        log.info("Category with id: {} deleted successfully", id);
    }

    @Override
    public boolean isCategoryNameExisting(String name) {
        boolean isCategoryNameExisting = categoryRepository.existsByName(name);
        log.info("Category '{}' exists in database: {}", name, isCategoryNameExisting);
        return isCategoryNameExisting;
    }

    @Override
    public boolean isCategoryPresent(Long id) {
        boolean isCategoryPresent = categoryRepository.existsById(id);
        log.info("is category id '{}' present in database: {}", id, isCategoryPresent);
        return isCategoryPresent;
    }
}
