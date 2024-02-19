package com.example.Cat.Shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Cat.Shop.model.Category;
import com.example.Cat.Shop.repository.CategoryRepo;

@Service
public class CategoryService {
	@Autowired
	CategoryRepo cateRepo;
	
	public List<Category> getAllCategories(){
		return cateRepo.findAll();
	}
	
	public void addCategory(Category category) {
		cateRepo.save(category);
	}
	
	public void deleteCategory(Integer id) {
		cateRepo.deleteById(id);
	}
	
	public Category getCategory(Integer id){
		return cateRepo.findById(id).orElse(null);
	}
}
