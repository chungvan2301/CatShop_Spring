package com.example.Cat.Shop.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;

    private String province;
    private String district;
    private String ward;
    private String phoneNumber;
    private String streetAndDepartment;
    private String type;
    private Boolean addressDefault;
    private String nameReceiver;


}
