package com.umg.proyecto.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Integer id;
    private String names;
    private String lastNames;
    private String phoneNumber;
    private String address;
    private Character status;
    private String email;
    private String password;
}