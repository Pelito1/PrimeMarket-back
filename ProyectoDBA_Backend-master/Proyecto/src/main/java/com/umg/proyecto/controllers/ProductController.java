package com.umg.proyecto.controllers;

import com.umg.proyecto.models.Product;
import com.umg.proyecto.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Integer id) {
        Product product = productService.findById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody Product product) {
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable("id") Integer id, @RequestBody Product product) {
        product.setId(id);
        productService.update(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar un producto por ID
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id) {
      productService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // **Nuevo método para buscar productos por palabra clave**
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable("keyword") String keyword) {
        List<Product> products = productService.searchByKeyword(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterProducts(
            @RequestParam int minPrice,
            @RequestParam int maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice, page, size);
        int totalProducts = productService.countProductsByPriceRange(minPrice, maxPrice);
        boolean hasMore = page * size < totalProducts;

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("totalProducts", totalProducts);
        response.put("hasMore", hasMore);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getPaginatedProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "52") int size
    ) {
        int totalProducts = productService.countAllProducts(); // Llamada al método que cuenta los productos
        List<Product> products = (List<Product>) getPaginatedProducts(page, size);
        boolean hasMore = products.size() == size;

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("totalProducts", totalProducts);
        response.put("hasMore", hasMore);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/category/{categoryId}")
    public ResponseEntity<String> updateProductWithCategory(
            @PathVariable("id") Integer productId,
            @PathVariable("categoryId") Integer categoryId,
            @RequestBody Product product) {

        try {
            product.setId(productId);
            productService.updateProductWithCategory(product, categoryId);
            return new ResponseEntity<>("Product and category updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating product and category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteProductWithCategories(@PathVariable("id") Integer productId) {
        try {
            productService.deleteProductWithCategories(productId);
            return new ResponseEntity<>("Product and its categories deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting product and categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
