package com.productsService.business.service;


import com.productsService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> findProductById(Long id);

    Product saveProduct(Product product);

    Product editProduct(Long id, Product updatedProduct);

    void deleteProductById(Long id);

    boolean isProductPresent(Long id);

    List<Long> findExistingProducts(List<Long> productIds);
}
