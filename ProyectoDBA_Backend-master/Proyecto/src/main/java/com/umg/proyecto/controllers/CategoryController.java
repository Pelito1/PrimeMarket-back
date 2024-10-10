package com.umg.proyecto.controllers;

import com.umg.proyecto.models.Category;
import com.umg.proyecto.models.Product;
import com.umg.proyecto.services.CategoryProductService;
import com.umg.proyecto.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryProductService categoryProductService;

    // Obtener todas las categorías
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Integer id) {
        Category category = categoryService.findById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // Crear una nueva categoría
    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar una categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("id") Integer id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar una categoría por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
        categoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener subcategorías por ID de categoría padre
    @GetMapping("/parent/{parentCategoryId}")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable("parentCategoryId") Integer parentCategoryId) {
        List<Category> subcategories = categoryService.findSubcategories(parentCategoryId);
        return new ResponseEntity<>(subcategories, HttpStatus.OK);
    }

    // Obtener productos asociados a una categoría
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable("categoryId") Integer categoryId) {
        List<Product> products = categoryProductService.getProductsByCategory(categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Asociar un producto a una categoría
    @PostMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<Void> addProductToCategory(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        categoryProductService.addProductToCategory(productId, categoryId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Eliminar la asociación de un producto a una categoría
    @DeleteMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<Void> removeProductFromCategory(@PathVariable Integer categoryId, @PathVariable Integer productId) {
        categoryProductService.removeProductFromCategory(productId, categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
