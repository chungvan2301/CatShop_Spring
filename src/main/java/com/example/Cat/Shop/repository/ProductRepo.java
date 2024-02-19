package com.example.Cat.Shop.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Cat.Shop.model.Product;

@Repository
public interface ProductRepo extends JpaRepository <Product, Long> {
	Page<Product> findByCategoryId(int id, Pageable pageable);
	List <Product> findByNameContainingIgnoreCase(String keyword);
	List <Product> findByNameContainingIgnoreCaseAndCategory_Id(String keyword, int id);



	List <Product> findAllByPriceBetweenAndProductStatusIn(int minPrice, int maxPrice, Set<String> productStatuses);
	List <Product> findAllByPriceBetweenAndProductStatusIn(int minPrice, int maxPrice, Set<String> productStatuses, Pageable pageable);


	List<Product> findByCategoryIdAndPriceBetweenAndProductStatusIn(int id, int minPrice, int maxPrice, Set<String> productStatuses);
	List<Product> findByCategoryIdAndPriceBetweenAndProductStatusIn(int id, int minPrice, int maxPrice, Set<String> productStatuses, Pageable pageable);

	List<Product> findTop4ByProductStatusOrderByAddDateDesc (String productStatus);

	List<Product> findTop4ByOrderByQuantitySoldDesc();

}
