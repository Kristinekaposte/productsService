package com.productsService.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productsService.business.service.CategoryService;
import com.productsService.model.Category;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
public class CategoryControllerTest {
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    public static final String URL = "/api/v1/category";
    public static final String URL1 = URL + "/allCategories";
    public static final String URL2 = URL + "/getById";
    public static final String URL3 = URL + "/save";
    public static final String URL4 = URL + "/edit";
    public static final String URL5 = URL + "/delete";

    private Category category;
    private List<Category> categoryList;
    private Category updatedCategory;

    @BeforeEach
    public void init() {
        category = createCategory();
        categoryList = createCategoryList(category);
        updatedCategory = createUpdatedCategory();
    }

    @Test
    public void testGetAllCategories_Successful() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(categoryList);
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(categoryList.size())))
                .andExpect(jsonPath("$[0].id").value(categoryList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(categoryList.get(0).getName()));
        verify(categoryService, times(1)).getAllCategories();
    }
    @Test
    public void testFindAllCategories_WhenListEmpty_Successful() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        verify(categoryService, times(1)).getAllCategories();
    }
    @Test
    public void testGetCategoryById_ExistingId_Successful() throws Exception {
        when(categoryService.findCategoryById(1L)).thenReturn(Optional.of(category));
        mockMvc.perform(get(URL2 + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
        verify(categoryService, times(1)).findCategoryById(1L);
    }
    @Test
    public void testGetCategoryById_NonExistingId_UnSuccessful() throws Exception {
        when(categoryService.findCategoryById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get(URL2 + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", "Category not found with ID: " + 99));
        verify(categoryService, times(1)).findCategoryById(99L);
    }

    @Test
    void testSaveCategory_Successful() throws Exception {
        when(categoryService.isCategoryNameExisting(category.getName())).thenReturn(false);
        when(categoryService.saveCategory(any())).thenReturn(category);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
        verify(categoryService, times(1)).isCategoryNameExisting(category.getName());
        verify(categoryService, times(1)).saveCategory(any());
    }
    @Test
    void testSaveCategory_DuplicateCategoryName_UnSuccessful() throws Exception {
        when(categoryService.isCategoryNameExisting(category.getName())).thenReturn(true);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Sorry, the category name " + category.getName() + " is already registered."));

        verify(categoryService, times(1)).isCategoryNameExisting(category.getName());
        verify(categoryService, times(0)).saveCategory(any());
    }

    @Test
    public void testSaveProduct_ValidationFailure_Unsuccessful() throws Exception {
        Category categoryEmpty = new Category();
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testEditCategoryById_Successful() throws Exception {
        when(categoryService.isCategoryPresent(1L)).thenReturn(true);
        when(categoryService.isCategoryNameExisting(updatedCategory.getName())).thenReturn(false);
        when(categoryService.editCategory(1L, updatedCategory)).thenReturn(updatedCategory);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCategory.getId()))
                .andExpect(jsonPath("$.name").value(updatedCategory.getName()));
        verify(categoryService, times(1)).isCategoryNameExisting("updated art");
        verify(categoryService, times(1)).editCategory(1L, updatedCategory);
    }

    @Test
    void testEditCategoryById_CategoryNotFound_Unsuccessful() throws Exception {
        when(categoryService.isCategoryPresent(1L)).thenReturn(false);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound());
        verify(categoryService, times(1)).isCategoryPresent(1L);
        verify(categoryService, times(0)).isCategoryNameExisting(updatedCategory.getName());
        verify(categoryService, times(0)).editCategory(anyLong(), any(Category.class));
    }

    @Test
    void testEditCategoryById_DuplicateName_Unsuccessful() throws Exception {
        when(categoryService.isCategoryPresent(1L)).thenReturn(true);
        when(categoryService.isCategoryNameExisting(updatedCategory.getName())).thenReturn(true);
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Sorry, the category name " + updatedCategory.getName() + " is already registered."));
        verify(categoryService, times(1)).isCategoryPresent(1L);
        verify(categoryService, times(1)).isCategoryNameExisting(updatedCategory.getName());
        verify(categoryService, times(0)).editCategory(anyLong(), any(Category.class));
    }

    @Test
    public void testEditProductById_ValidationFailure_Unsuccessful() throws Exception {
        Category invalidCategory = new Category();
        mockMvc.perform(put(URL4 + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Validation failed"));
        verify(categoryService, times(0)).editCategory(anyLong(), any());
    }

    @Test
    void testDeleteCategory_CategoryExists_Successful() throws Exception {
        when(categoryService.isCategoryPresent(1L)).thenReturn(true);
        mockMvc.perform(delete(URL5 + "/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Category entry with ID " + 1L + " deleted"));
        verify(categoryService, times(1)).isCategoryPresent(1L);
        verify(categoryService, times(1)).deleteCategoryById(1L);
    }

    @Test
    void testDeleteCategory_CategoryNotExists_Unsuccessful() throws Exception {
        when(categoryService.isCategoryPresent(1L)).thenReturn(false);
        mockMvc.perform(delete(URL5 + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found with ID: " + 1L));
        verify(categoryService, times(1)).isCategoryPresent(1L);
        verify(categoryService, times(0)).deleteCategoryById(1L);
    }

    private Category createCategory() {
        return new Category(1L, "art");
    }

    private List<Category> createCategoryList(Category category) {
        List<Category> list = new ArrayList<>();
        list.add(category);
        return list;
    }
    private Category createUpdatedCategory() {
        return new Category(null, "updated art");
    }
}
