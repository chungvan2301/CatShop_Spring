package com.example.Cat.Shop.model;


import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cate_id", referencedColumnName="cate_id")
	private Category category;
	private double price;
	
	private String description;
	
	private String imageName;

	private LocalDateTime addDate;

	@Column(columnDefinition = "VARCHAR(255) default 'new'")
	private String productStatus;

	private int quantityAdd;

	@Column(columnDefinition = "int default 0")
	private int quantitySold;

	private int quantityInStore;

	private double avgRating;

}
