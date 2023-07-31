package com.productsService.web.controller;

import com.productsService.business.service.CategoryService;
import com.productsService.business.service.ProductService;
import com.productsService.model.Product;
import com.productsService.swagger.DescriptionVariables;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Api(tags = DescriptionVariables.PRODUCTS)
@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/products")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/allProducts")
    @ApiOperation(value = "Finds all Products entries",
            notes = "Returns all Products entries from the database",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<Product>> getAllProductsEntries() {
        List<Product> list = productService.getAllProducts();
        if (list.isEmpty()) {
            log.info("Empty Product list found");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        log.info("List size: {}", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    @ApiOperation(value = "Find a Product by ID",
            notes = "Returns a single Product entry based on the provided ID",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})

    public ResponseEntity<Product> getProductById(@ApiParam(value = "id of the Product entry", required = true)
                                                    @PathVariable("id") Long id) {
        Optional<Product> productOptional = productService.findProductById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            log.info("Found Product with ID {}: {}", id, product);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }
        log.warn("Product not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                "Message", "Product not found with ID: " + id).build();
    }

    @PostMapping("/save")
    @ApiOperation(value = "Saves Product entry in database",
            notes = "Provide Product data to save.",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The request has create successfully"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<?> saveProduct(@RequestBody @Valid Product product) {
        if (!categoryService.isCategoryPresent(product.getCategoryId())) {
            log.warn("Cannot save Category entry with ID: {}, Category not found", product.getCategoryId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with ID: " + product.getCategoryId());
        }
        Product savedProduct = productService.saveProduct(product);
        log.info("Product entry saved: {}", savedProduct);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }


    @PutMapping("/edit/{id}")
    @ApiOperation(value = "Edits Product entry by ID",
            notes = "Provide an id to edit specific product in the database",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "The server has Bad Request, cannot process due to an invalid request"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<?> editProductById(@PathVariable Long id, @RequestBody @Valid Product updatedProduct) {
        if (!categoryService.isCategoryPresent(updatedProduct.getCategoryId())) {
            log.warn("Cannot save Category entry with ID: {}, Category not found", updatedProduct.getCategoryId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with ID: " + updatedProduct.getCategoryId());
        }
        Product editedProduct = productService.editProduct(id, updatedProduct);
        log.info("Product with ID {} updated successfully.", id);
        return ResponseEntity.ok(editedProduct);
    }



    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Deletes Product entry by ID",
            notes = "Provide an id to delete specific Product from the database",
            response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (productService.isProductPresent(id)) {
            productService.deleteProductById(id);
            log.info("Product entry with ID: {} deleted", id);
            return ResponseEntity.ok("Product entry with ID " + id + " deleted");
        }
        log.warn("Cannot delete Product entry with ID: {}, Product not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + id);
    }
}
