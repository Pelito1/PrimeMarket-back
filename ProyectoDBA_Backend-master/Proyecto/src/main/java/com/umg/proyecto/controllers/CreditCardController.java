package com.umg.proyecto.controllers;

import com.umg.proyecto.models.CreditCard;
import com.umg.proyecto.services.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    // Obtener todas las tarjetas de crédito
    @GetMapping
    public ResponseEntity<List<CreditCard>> getAllCreditCards() {
        List<CreditCard> creditCards = creditCardService.findAll();
        return new ResponseEntity<>(creditCards, HttpStatus.OK);
    }

    // Obtener una tarjeta de crédito por ID
    @GetMapping("/{id}")
    public ResponseEntity<CreditCard> getCreditCardById(@PathVariable("id") Integer id) {
        CreditCard creditCard = creditCardService.findById(id);
        return new ResponseEntity<>(creditCard, HttpStatus.OK);
    }

    // Obtener todas las tarjetas de crédito por ID de cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditCard>> getCreditCardsByCustomerId(@PathVariable("customerId") Integer customerId) {
        List<CreditCard> creditCards = creditCardService.findByCustomerId(customerId);
        return new ResponseEntity<>(creditCards, HttpStatus.OK);
    }

    // Crear una nueva tarjeta de crédito
    @PostMapping
    public ResponseEntity<Void> createCreditCard(@RequestBody CreditCard creditCard) {
        creditCardService.save(creditCard);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar una tarjeta de crédito existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCreditCard(@PathVariable("id") Integer id, @RequestBody CreditCard creditCard) {
        creditCard.setId(id);
        creditCardService.update(creditCard);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar una tarjeta de crédito por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable("id") Integer id) {
        creditCardService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{creditCardId}/purchase")
    public ResponseEntity<Void> purchase(@PathVariable("creditCardId") Integer creditCardId, @RequestParam("orderId") Integer orderId) {
        creditCardService.processPurchase(creditCardId, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
