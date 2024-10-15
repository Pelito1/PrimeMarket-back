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

    // Obtener todas las temporadas activas
    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        List<Season> seasons = seasonService.findActiveSeasons();
        return new ResponseEntity<>(seasons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Season> getSeasonById(@PathVariable Integer id) {
        Season season = seasonService.findById(id);
        if (season != null) {
            return new ResponseEntity<>(season, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Obtener productos por temporada
    @GetMapping("/{seasonId}/products")
    public ResponseEntity<List<Product>> getProductsBySeason(@PathVariable Integer seasonId) {
        List<Product> products = seasonProductService.getProductsBySeason(seasonId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Actualizar estado de temporada
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateSeasonStatus(@PathVariable Integer id, @RequestBody Season season) {
        seasonService.updateStatus(id, season.getStatus());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Season> createSeason(@RequestBody Season season) {
        seasonService.save(season);
        return new ResponseEntity<>(season, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSeason(@PathVariable Integer id, @RequestBody Season season) {
        season.setId(id);
        seasonService.update(season);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable Integer id) {
        seasonService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/{seasonId}/products/{productId}")
    public ResponseEntity<Void> addProductToSeason(@PathVariable Integer seasonId, @PathVariable Integer productId) {
        seasonProductService.addProductToSeason(productId, seasonId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @DeleteMapping("/{seasonId}/products/{productId}")
    public ResponseEntity<Void> removeProductFromSeason(@PathVariable Integer seasonId, @PathVariable Integer productId) {
        seasonProductService.removeProductFromSeason(productId, seasonId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Season>> getAllSeasonsUnfiltered() {
        List<Season> seasons = seasonService.findAllSeasons();
        return new ResponseEntity<>(seasons, HttpStatus.OK);
    }

}
