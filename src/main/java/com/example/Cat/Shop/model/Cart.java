package com.example.Cat.Shop.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "ID")
    private Product product;

    private int quantity;

    private int sold;


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
