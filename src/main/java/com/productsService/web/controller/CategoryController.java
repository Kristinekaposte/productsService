package com.productsService.web.controller;

import com.productsService.business.service.CategoryService;
import com.productsService.model.Category;
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

@Api(tags = DescriptionVariables.CATEGORY)
@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/allCategories")
    @ApiOperation(value = "Finds all Category entries",
            notes = "Returns all Category entries from the database",
            response = Category.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<Category>> getAllCategoryEntries() {
        List<Category> list = categoryService.getAllCategories();
        if (list.isEmpty()) {
            log.info("Empty Category list found");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        log.info("List size: {}", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    @ApiOperation(value = "Find a Category by ID",
            notes = "Returns a single Category entry based on the provided ID",
            response = Category.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})

    public ResponseEntity<Category> getCategoryById(@ApiParam(value = "id of the Category entry", required = true)
                                                    @PathVariable("id") Long id) {
        Optional<Category> categoryOptional = categoryService.findCategoryById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            log.info("Found Category with ID {}: {}", id, category);
            return ResponseEntity.status(HttpStatus.OK).body(category);
        }
        log.warn("Category not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                "Message", "Category not found with ID: " + id).build();
    }

    @PostMapping("/save")
    @ApiOperation(value = "Saves Category entry in database",
            notes = "Provide Category data to save.",
            response = Category.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The request has create successfully"),
            @ApiResponse(code = 400, message = "The server has Bad Request, cannot process due invalid request"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<?> saveCategory(@RequestBody @Valid Category category) {
        String categoryName = category.getName();
        if (categoryService.isCategoryNameExisting(categoryName)) {
            log.info("The Category name " + categoryName +" is already registered");
            return new ResponseEntity<>("Sorry, the category name " + categoryName + " is already registered.", HttpStatus.BAD_REQUEST);
        }
        Category savedCategory = categoryService.saveCategory(category);
        log.info("Category entry saved: {}", savedCategory);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    @ApiOperation(value = "Edits Category entry by ID",
            notes = "Provide an id to edit specific category in the database",
            response = Category.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "The server has Bad Request, cannot process due to an invalid request"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<?> editCategoryById(@PathVariable Long id, @RequestBody @Valid Category updatedCategory) {
        if (!categoryService.isCategoryPresent(id)) {
            log.warn("Sorry, the category with id " + id + " does not exist.");
            return new ResponseEntity<>("Sorry, the category id " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }
        String categoryName = updatedCategory.getName();
        if (categoryService.isCategoryNameExisting(categoryName)) {
            log.info("The Category name " + categoryName +" is already registered");
            return new ResponseEntity<>("Sorry, the category name " + categoryName + " is already registered.", HttpStatus.BAD_REQUEST);
        }
        Category editedCategory = categoryService.editCategory(id, updatedCategory);
        log.info("Category with ID {} updated successfully.", id);
        return ResponseEntity.ok(editedCategory);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "Deletes Category entry by ID",
            notes = "Provide an id to delete specific Category from the database",
            response = Category.class) // BEFORE WAS String.class
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        if (categoryService.isCategoryPresent(id)) {
            categoryService.deleteCategoryById(id);
            log.info("Category entry with ID: {} deleted", id);
            return ResponseEntity.ok("Category entry with ID " + id + " deleted");
        }
        log.warn("Cannot delete Category entry with ID: {}, category not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with ID: " + id);
    }
}
