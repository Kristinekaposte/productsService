package com.productsService.business.service.impl;

import com.productsService.business.mappers.ProductMapper;
import com.productsService.business.repository.ProductRepository;
import com.productsService.business.repository.model.ProductDAO;
import com.productsService.model.Product;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDAO productDAO;
    private Product product;
    private ProductDAO oldProductDAO;
    private List<ProductDAO> productDAOList;

    @BeforeEach
    public void init() {
        productDAO = createProductDAO();
        product = createProduct();
        oldProductDAO = createOldProductDAO();
        productDAOList = createProductDAOList(productDAO);
    }

    @Test
     void testGetAllProductsEntries_Successful() {
        when(productRepository.findAll()).thenReturn(productDAOList);
        when(productMapper.daoToProduct(productDAO)).thenReturn(product);
        List<Product> list = productService.getAllProducts();
        assertEquals(2, list.size());
        assertEquals(product.getId(), list.get(0).getId());
        verify(productRepository, times(1)).findAll();
    }

    @Test
     void testGetAllProducts_ListEmpty_Successful() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        List<Product> result = productService.getAllProducts();
        verify(productRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }


    @Test
     void findProductById_Successful() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productDAO));
        when(productMapper.daoToProduct(productDAO)).thenReturn(product);
        Optional<Product> actualResult = productService.findProductById(1L);
        assertTrue(actualResult.isPresent());
        assertEquals(product, actualResult.get());
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).daoToProduct(productDAO);
    }

    @Test
     void testFindProductById_NonExistingId_Failed() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.findProductById(99L);
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
     void saveProduct_Successful() {
        when(productMapper.productToDAO(product)).thenReturn(productDAO);
        when(productRepository.save(productDAO)).thenReturn(productDAO);
        when(productMapper.daoToProduct(productDAO)).thenReturn(product);
        Product savedProduct = productService.saveProduct(product);
        assertEquals(product, savedProduct);
        verify(productMapper).productToDAO(product);
        verify(productRepository, times(1)).save(productDAO);
        verify(productMapper).daoToProduct(productDAO);
    }

    @Test
     void testEditProductById_Successful() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(oldProductDAO));
        when(productRepository.save(oldProductDAO)).thenReturn(productDAO);
        when(productMapper.daoToProduct(productDAO)).thenReturn(product);
        Product result = productService.editProduct(1L, product);
        assertNotNull(result);
        assertEquals(product, result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(oldProductDAO);
        verify(productMapper, times(1)).daoToProduct(productDAO);
    }

    @Test
     void testEditProductById_NonExistingId_Failed() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        Product result = productService.editProduct(99L, product);
        assertNull(result);
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).daoToProduct(any());
    }

    @Test
     void testDeleteProductById_Successful() {
        productService.deleteProductById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
     void testIsProductPresent_ProductExists_Successful() {
        when(productRepository.existsById(1L)).thenReturn(true);
        boolean isPresent = productService.isProductPresent(1L);
        verify(productRepository, times(1)).existsById(1L);
        assertTrue(isPresent);
    }

    @Test
     void testIsProductPresent_ProductDoesNotExist_UnSuccessful() {
        when(productRepository.existsById(99L)).thenReturn(false);
        boolean isPresent = productService.isProductPresent(99L);
        verify(productRepository, times(1)).existsById(99L);
        assertFalse(isPresent);
    }

    private ProductDAO createProductDAO() {
        return new ProductDAO(1L, "Radio new", "small new radio", 4.99, 4, 1L);
    }

    private Product createProduct() {
        return new Product(1L, "Radio new", "small new radio", 4.99, 4, 1L);
    }

    private ProductDAO createOldProductDAO() {
        return new ProductDAO(1L, "Radio old", "medium old radio", 9.99, 6, 1L);
    }

    private List<ProductDAO> createProductDAOList(ProductDAO productDAO) {
        List<ProductDAO> list = new ArrayList<>();
        list.add(productDAO);
        list.add(productDAO);
        return list;
    }

}
