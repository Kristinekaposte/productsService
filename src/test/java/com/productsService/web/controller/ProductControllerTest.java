package com.productsService.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productsService.business.service.CategoryService;
import com.productsService.business.service.ProductService;
import com.productsService.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @MockBean
    private ProductService productService;
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    public static final String URL = "/api/v1/products";
    public static final String URL1 = URL + "/allProducts";
    public static final String URL2 = URL + "/getById";
    public static final String URL3 = URL + "/save";
    public static final String URL4 = URL + "/edit";
    public static final String URL5 = URL + "/delete";
    public static final String URL6 = URL + "/getProductInfo";

    private Product product;
    private List<Product> productList;
    private Product updatedProduct;
    private Product savedProduct;

    @BeforeEach
    public void init() {
        product = createProduct();
        productList = createProductList(product);
        savedProduct = createSavedProduct();
        updatedProduct = createUpdatedProduct();
    }

    @Test
    void testGetAllProducts_Successful() throws Exception {
        when(productService.getAllProducts()).thenReturn(productList);
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(productList.size())))
                .andExpect(jsonPath("$[0].id").value(productList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(productList.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(productList.get(0).getDescription()))
                .andExpect(jsonPath("$[0].price").value(productList.get(0).getPrice()))
                .andExpect(jsonPath("$[0].quantity").value(productList.get(0).getQuantity()))
                .andExpect(jsonPath("$[0].categoryId").value(productList.get(0).getCategoryId()));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testFindAllProducts_WhenListEmpty_Successful() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_ExistingId_Successful() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(product));
        mockMvc.perform(get(URL2 + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.categoryId").value(product.getCategoryId()));
        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    void testGetProductById_NonExistingId_UnSuccessful() throws Exception {
        when(productService.findProductById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get(URL2 + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", "Product not found with ID: " + 99));
        verify(productService, times(1)).findProductById(99L);
    }

    @Test
    void testSaveProduct_Successful() throws Exception {
        when(categoryService.isCategoryPresent(savedProduct.getCategoryId())).thenReturn(true);
        when(productService.saveProduct(any())).thenReturn(savedProduct);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value(savedProduct.getName()))
                .andExpect(jsonPath("$.description").value(savedProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
                .andExpect(jsonPath("$.quantity").value(savedProduct.getQuantity()))
                .andExpect(jsonPath("$.categoryId").value(savedProduct.getCategoryId()));
        verify(productService, times(1)).saveProduct(savedProduct);
    }

    @Test
    void testSaveProduct_CategoryNotFound_UnSuccessful() throws Exception {
        when(categoryService.isCategoryPresent(99L)).thenReturn(false);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedProduct)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Category not found with ID: " + savedProduct.getCategoryId()));
        verify(productService, never()).saveProduct(any());
    }

    @Test
    void testSaveProduct_ValidationFailure_Unsuccessful() throws Exception {
        Product productEmpty = new Product();
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testEditProductById_Successful() throws Exception {
        when(categoryService.isCategoryPresent(updatedProduct.getCategoryId())).thenReturn(true);
        when(productService.editProduct(1L, updatedProduct)).thenReturn(updatedProduct);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedProduct.getId()))
                .andExpect(jsonPath("$.name").value(updatedProduct.getName()))
                .andExpect(jsonPath("$.description").value(updatedProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(updatedProduct.getPrice()))
                .andExpect(jsonPath("$.quantity").value(updatedProduct.getQuantity()))
                .andExpect(jsonPath("$.categoryId").value(updatedProduct.getCategoryId()));
        verify(categoryService, times(1)).isCategoryPresent(updatedProduct.getCategoryId());
        verify(productService, times(1)).editProduct(1L, updatedProduct);
    }

    @Test
    void testEditProductById_CategoryNotFound_Unsuccessful() throws Exception {
        when(categoryService.isCategoryPresent(updatedProduct.getCategoryId())).thenReturn(false);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found with ID: " + updatedProduct.getCategoryId()));
        verify(categoryService, times(1)).isCategoryPresent(updatedProduct.getCategoryId());
        verify(productService, never()).editProduct(1L, updatedProduct);
    }

    @Test
    void testEditProductById_ValidationFailure_Unsuccessful() throws Exception {
        Product invalidProduct = new Product();
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
        verify(productService, times(0)).editProduct(anyLong(), any());
    }

    @Test
    void testDeleteProduct_Successful() throws Exception {
        when(productService.isProductPresent(1L)).thenReturn(true);
        mockMvc.perform(delete(URL5 + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product entry with ID " + 1L + " deleted"));
        verify(productService, times(1)).deleteProductById(1L);
    }

    @Test
    void testDeleteProduct_ProductNotFound_Unsuccessful() throws Exception {
        when(productService.isProductPresent(99L)).thenReturn(false);
        mockMvc.perform(delete(URL5 + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found with ID: " + 99L));
        verify(productService, never()).deleteProductById(99L);
    }

    @Test
    void testGetProductInfo_Successful() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        Map<Long, Double> productInfo = new HashMap<>();
        productInfo.put(1L, 4.99);
        productInfo.put(3L, 4.99);
        when(productService.getProductInfo(productIds)).thenReturn(productInfo);
        mockMvc.perform(get(URL6)
                        .param("productIds", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.1").value(4.99));
        verify(productService, times(1)).getProductInfo(productIds);
    }

    @Test
    void testGetProductInfo_NoProductsInfoFound_Unsuccessful() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        Map<Long, Double> emptyProductInfo = new HashMap<>();
        when(productService.getProductInfo(productIds)).thenReturn(emptyProductInfo);
        mockMvc.perform(get(URL6)
                        .param("productIds", "1", "2", "3"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", "No productInfo found for the provided product IDs: [1, 2, 3]. list size: 0"));
        verify(productService, times(1)).getProductInfo(productIds);
    }

    private Product createProduct() {
        return new Product(1L, "Radio new", "small new radio", 4.99, 4, 1L);
    }

    private Product createSavedProduct() {
        return new Product(null, "new product", "fresh new product", 5.22, 5, 2L);
    }

    private Product createUpdatedProduct() {
        return new Product(null, "updated product", "new updated product", 7.22, 7, 1L);
    }

    private List<Product> createProductList(Product product) {
        List<Product> list = new ArrayList<>();
        list.add(product);
        list.add(product);
        return list;
    }
}
