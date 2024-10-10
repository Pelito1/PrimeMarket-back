package com.umg.proyecto.controllers;

import com.umg.proyecto.models.Product;
import com.umg.proyecto.models.Season;
import com.umg.proyecto.services.SeasonProductService;
import com.umg.proyecto.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private SeasonProductService seasonProductService;

    // Obtener todas las temporadas
    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        List<Season> seasons = seasonService.findAll();
        return new ResponseEntity<>(seasons, HttpStatus.OK);
    }

    // Obtener una temporada por ID
    @GetMapping("/{id}")
    public ResponseEntity<Season> getSeasonById(@PathVariable("id") Integer id) {
        Season season = seasonService.findById(id);
        return new ResponseEntity<>(season, HttpStatus.OK);
    }

    // Crear una nueva temporada
    @PostMapping
    public ResponseEntity<Void> createSeason(@RequestBody Season season) {
        seasonService.save(season);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar una temporada existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSeason(@PathVariable("id") Integer id, @RequestBody Season season) {
        season.setId(id);
        seasonService.update(season);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar una temporada por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable("id") Integer id) {
        seasonService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener productos asociados a una temporada
    @GetMapping("/{seasonId}/products")
    public ResponseEntity<List<Product>> getProductsBySeason(@PathVariable("seasonId") Integer seasonId) {
        List<Product> products = seasonProductService.getProductsBySeason(seasonId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Vincular un producto a una temporada
    @PostMapping("/{seasonId}/products/{productId}")
    public ResponseEntity<Void> addProductToSeason(@PathVariable Integer seasonId, @PathVariable Integer productId) {
        seasonProductService.addProductToSeason(productId, seasonId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Eliminar la vinculación de un producto de una temporada
    @DeleteMapping("/{seasonId}/products/{productId}")
    public ResponseEntity<Void> removeProductFromSeason(@PathVariable Integer seasonId, @PathVariable Integer productId) {
        seasonProductService.removeProductFromSeason(productId, seasonId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}