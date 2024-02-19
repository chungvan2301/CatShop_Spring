package com.example.Cat.Shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Cat.Shop.model.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
	
}
