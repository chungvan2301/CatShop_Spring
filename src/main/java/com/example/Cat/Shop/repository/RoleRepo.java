package com.example.Cat.Shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Cat.Shop.model.Role;

public interface RoleRepo extends JpaRepository <Role, Integer> {
	
}
