package com.productsService.business.service;


import com.productsService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> findProductById(Long id);

    Product saveProduct(Product product);

    Product editProduct(Long id, Product UpdatedProduct);

    void deleteProductById(Long id);

    boolean isProductPresent(Long id);
}
