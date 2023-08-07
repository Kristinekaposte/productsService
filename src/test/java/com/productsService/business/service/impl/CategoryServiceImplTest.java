package com.productsService.business.service.impl;

import com.productsService.business.mappers.CategoryMapper;
import com.productsService.business.repository.CategoryRepository;
import com.productsService.business.repository.ProductRepository;
import com.productsService.business.repository.model.CategoryDAO;
import com.productsService.business.repository.model.ProductDAO;
import com.productsService.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDAO categoryDAO;
    private Category category;
    private CategoryDAO oldCategoryDAO;
    private List<CategoryDAO> categoryDAOList;
    private ProductDAO productDAO;
    private List<ProductDAO> productDAOList;
    private CategoryDAO categoryDAOWithRelatedProducts;

    @BeforeEach
    public void init() {
        categoryDAO = createCategoryDAO();
        category = createCategory();
        oldCategoryDAO = createOldCategoryDAO();
        categoryDAOList = createCategoryDAOList(categoryDAO);
        productDAO = createProductDAO();
        productDAOList =createProductDAOList(productDAO);
        categoryDAOWithRelatedProducts =createCategoryDAOWithRelatedProducts(productDAOList);
    }

    @Test
     void testGetAllCategoryEntries_Successful() {
        when(categoryRepository.findAll()).thenReturn(categoryDAOList);
        when(categoryMapper.daoToCategory(categoryDAO)).thenReturn(category);
        List<Category> list = categoryService.getAllCategories();
        assertEquals(1, list.size());
        assertEquals(category.getId(), list.get(0).getId());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
     void testGetAllCategory_ListEmpty_Successful() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        List<Category> result = categoryService.getAllCategories();
        verify(categoryRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
     void findCategoryById_Successful() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryDAO));
        when(categoryMapper.daoToCategory(categoryDAO)).thenReturn(category);
        Optional<Category> actualResult = categoryService.findCategoryById(1L);
        assertTrue(actualResult.isPresent());
        assertEquals(category, actualResult.get());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryMapper, times(1)).daoToCategory(categoryDAO);
    }

    @Test
     void testFindCategoryById_NonExistingId_Failed() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Category> result = categoryService.findCategoryById(99L);
        assertFalse(result.isPresent());
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
     void saveCategory_Successful() {
        when(categoryMapper.categoryToDAO(category)).thenReturn(categoryDAO);
        when(categoryRepository.save(categoryDAO)).thenReturn(categoryDAO);
        when(categoryMapper.daoToCategory(categoryDAO)).thenReturn(category);
        Category savedCategory = categoryService.saveCategory(category);
        assertEquals(category, savedCategory);
        verify(categoryMapper).categoryToDAO(category);
        verify(categoryRepository, times(1)).save(categoryDAO);
        verify(categoryMapper).daoToCategory(categoryDAO);
    }

    @Test
     void testEditCategoryById_Successful() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(oldCategoryDAO));
        when(categoryRepository.save(oldCategoryDAO)).thenReturn(categoryDAO);
        when(categoryMapper.daoToCategory(categoryDAO)).thenReturn(category);
        Category result = categoryService.editCategory(1L, category);
        assertNotNull(result);
        assertEquals(category, result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(oldCategoryDAO);
        verify(categoryMapper, times(1)).daoToCategory(categoryDAO);
    }

    @Test
     void testEditCategoryById_NonExistingId_Failed() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        Category result = categoryService.editCategory(99L, category);
        assertNull(result);
        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).daoToCategory(any());
    }

    @Test
     void testDeleteCategoryById_Successful_WithRelatedProducts() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryDAOWithRelatedProducts));
        categoryService.deleteCategoryById(1L);
        verify(productRepository, times(1)).deleteAll(categoryDAOWithRelatedProducts.getProducts());
        verify(categoryRepository, times(1)).delete(categoryDAOWithRelatedProducts);
    }
    @Test
     void testDeleteCategoryById_Successful_WithoutRelatedProducts() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryDAO));
        categoryService.deleteCategoryById(1L);
        verify(productRepository, never()).deleteAll(anyList());
        verify(categoryRepository, times(1)).delete(categoryDAO);
    }



    @Test
     void testIsCategoryNameExisting_CategoryNameExists() {
        when(categoryRepository.existsByName(category.getName())).thenReturn(true);
        boolean result = categoryService.isCategoryNameExisting(category.getName());
        assertTrue(result);
        verify(categoryRepository, times(1)).existsByName(category.getName());
    }

    @Test
     void testIsCategoryNameExisting_CategoryNameDoesNotExist() {
        when(categoryRepository.existsByName(category.getName())).thenReturn(false);
        boolean result = categoryService.isCategoryNameExisting(category.getName());
        assertFalse(result);
        verify(categoryRepository, times(1)).existsByName(category.getName());
    }

    @Test
     void testIsCategoryPresent_CategoryExists() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        boolean result = categoryService.isCategoryPresent(1L);
        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
     void testIsCategoryPresent_CategoryDoesNotExist() {
        when(categoryRepository.existsById(99L)).thenReturn(false);
        boolean result = categoryService.isCategoryPresent(99L);
        assertFalse(result);
        verify(categoryRepository, times(1)).existsById(99L);
    }


    private CategoryDAO createCategoryDAO() {
        return new CategoryDAO(1L, "art");
    }

    private Category createCategory() {
        return new Category(1L, "art");
    }

    private CategoryDAO createOldCategoryDAO() {
        return new CategoryDAO(1L, "old entry");
    }

    private List<CategoryDAO> createCategoryDAOList(CategoryDAO categoryDAO) {
        List<CategoryDAO> list = new ArrayList<>();
        list.add(categoryDAO);
        return list;
    }

    private ProductDAO createProductDAO() {
        return new ProductDAO(1L, "Radio", "small radio", 4.99, 4, 1L);
    }
    private List<ProductDAO> createProductDAOList(ProductDAO productDAO) {
        List<ProductDAO> list = new ArrayList<>();
        list.add(productDAO);
        return list;
    }
    private CategoryDAO createCategoryDAOWithRelatedProducts(List<ProductDAO> listOfProducts) {
        return new CategoryDAO(1L, "art", listOfProducts);
    }
}
