package com.umg.proyecto.controllers;

import com.umg.proyecto.models.Brand;
import com.umg.proyecto.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Obtener todas las marcas
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        List<Brand> brands = brandService.findAll();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    // Obtener una marca por ID
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.findById(id);
        return new ResponseEntity<>(brand, HttpStatus.OK);
    }

    // Crear una nueva marca
    @PostMapping
    public ResponseEntity<String> createBrand(@RequestBody Brand brand) {
        brandService.save(brand);
        return new ResponseEntity<>("Brand created successfully", HttpStatus.CREATED);
    }

    // Actualizar una marca existente
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        brand.setId(id); // Aseguramos que el ID es el correcto
        brandService.update(brand);
        return new ResponseEntity<>("Brand updated successfully", HttpStatus.OK);
    }

    // Eliminar una marca por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Integer id) {
        brandService.delete(id);
        return new ResponseEntity<>("Brand deleted successfully", HttpStatus.OK);
    }
}