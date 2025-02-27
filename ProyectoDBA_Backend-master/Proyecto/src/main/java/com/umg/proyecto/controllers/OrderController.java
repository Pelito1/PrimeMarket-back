package com.umg.proyecto.controllers;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.umg.proyecto.models.Order;
import com.umg.proyecto.models.OrderDetail;
import com.umg.proyecto.models.OrderRequest;
import com.umg.proyecto.services.OrderDetailService;
import com.umg.proyecto.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;
    @CrossOrigin(origins = "http://localhost:3000")
    // Obtener todas las órdenes
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Obtener una orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Integer id) {
        Order order = orderService.findById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // Obtener todas las órdenes de un cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable("customerId") Integer customerId) {
        List<Order> orders = orderService.findByCustomerId(customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Crear una nueva orden
    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {
        orderService.save(order);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar una orden existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable("id") Integer id, @RequestBody Order order) {
        order.setId(id);
        orderService.update(order);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    // Eliminar una orden por ID
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Integer id) {
//        orderService.delete(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    // Obtener todos los detalles de una orden
    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable("orderId") Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        return new ResponseEntity<>(orderDetails, HttpStatus.OK);
    }

    // Agregar un detalle de orden
    @PostMapping("/{orderId}/details")
    public ResponseEntity<Void> addOrderDetail(@PathVariable("orderId") Integer orderId, @RequestBody OrderDetail orderDetail) {
        orderDetail.setOrderId(orderId);
        orderDetailService.save(orderDetail);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Actualizar un detalle de orden
    @PutMapping("/{orderId}/details/{productId}")
    public ResponseEntity<Void> updateOrderDetail(@PathVariable("orderId") Integer orderId, @PathVariable("productId") Integer productId, @RequestBody OrderDetail orderDetail) {
        orderDetail.setOrderId(orderId);
        orderDetail.setProductId(productId);
        orderDetailService.update(orderDetail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Eliminar un detalle de orden
    @DeleteMapping("/{orderId}/details/{productId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable("orderId") Integer orderId, @PathVariable("productId") Integer productId) {
        orderDetailService.delete(orderId, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.processOrder(orderRequest);
            return new ResponseEntity<>("Order processed successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrderAndDetails(@PathVariable("orderId") Integer orderId) {
        try {
            orderService.deleteOrderAndDetails(orderId);
            return new ResponseEntity<>("Order and its details deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/order-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable("orderId") Integer orderId,
            @RequestBody String purchaseStatus) {
        try {
            orderService.updateOrderStatus(orderId, purchaseStatus);
            return new ResponseEntity<>("Order status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating order status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}