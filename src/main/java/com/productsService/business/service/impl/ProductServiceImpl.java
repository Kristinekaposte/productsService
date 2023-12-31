package com.productsService.business.service.impl;

import com.productsService.business.mappers.ProductMapper;
import com.productsService.business.repository.ProductRepository;
import com.productsService.business.repository.model.ProductDAO;
import com.productsService.business.service.ProductService;
import com.productsService.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = productRepository.findAll()
                .stream()
                .map(productMapper::daoToProduct)
                .collect(Collectors.toList());
        log.info("Size of the Product list: {}", list.size());
        return list;
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        Optional<ProductDAO> productDAO = productRepository.findById(id);
        if (!productDAO.isPresent()) {
            log.info("Product with id {} does not exist.", id);
            return Optional.empty();
        }
        log.info("Product with id {} found.", id);
        return productDAO.map(productMapper::daoToProduct);
    }

    @Override
    public Product saveProduct(Product product) {
        log.info("Saving Product entry: {}", product);
        return productMapper.daoToProduct(productRepository.save(productMapper.productToDAO(product)));
    }

    @Override
    public Product editProduct(Long id, Product updatedProduct) {
        Optional<ProductDAO> optionalProductDAO = productRepository.findById(id);
        if (optionalProductDAO.isPresent()) {
            ProductDAO existingProductDAO = optionalProductDAO.get();
            BeanUtils.copyProperties(updatedProduct, existingProductDAO, "id");

            Product updatedProductObject = productMapper.daoToProduct(productRepository.save(existingProductDAO));
            log.info("Product entry with ID: {} updated", id);
            return updatedProductObject;
        } else
            log.warn("Failed to update product. Product entry with ID: {} not found", id);
        return null;
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
        log.info("Product entry with id: {} is deleted", id);
    }

    @Override
    public boolean isProductPresent(Long id) {
        boolean isProductPresent = productRepository.existsById(id);
        log.info("is product id '{}' present in database: {}", id, isProductPresent);
        return isProductPresent;
    }

    /**
     * Gets the price of products based on their IDs from request.
     *
     * @param productIds List of productIds for which to get prices.
     * @return Map containing productIds as keys and their price as value.
     */
    @Override
    public Map<Long, Double> getProductInfo(List<Long> productIds) {
        Map<Long, Double> productInfo = new HashMap<>();
        for (Long productId : productIds) {
            ProductDAO productDAO = productRepository.findById(productId).orElse(null);
            if (productDAO != null) {
                Product product = productMapper.daoToProduct(productDAO);
                productInfo.put(productId, product.getPrice());
            }
        }
        log.info("Returning productInfo for these productIds: {}", productInfo.keySet());
        return productInfo;
    }

}
