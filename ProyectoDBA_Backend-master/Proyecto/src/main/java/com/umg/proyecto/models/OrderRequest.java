package com.umg.proyecto.models;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderRequest {
    private Integer customerId;  // Null si el cliente no ha iniciado sesión
    private Customer customer;   // Datos del cliente para registro si no está registrado
    private List<OrderDetail> orderDetails;
    private String status;  // Ej.: "Pendiente"
    private Float total;

    // Getters y setters
}
