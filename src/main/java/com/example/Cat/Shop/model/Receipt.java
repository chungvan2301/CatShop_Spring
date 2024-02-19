package com.example.Cat.Shop.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateCreated;
    private LocalDateTime dateCreatedDateFormat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false, referencedColumnName = "id")
    private Address address;

    @ElementCollection
    private List<Cart> cartsList = new ArrayList<Cart>();

    private double goodsFee;

    private double transportFee;

    private double totalPrice;

    private String receiptCode;

    private String paymentMethod;

    private String dayReceived;

}
