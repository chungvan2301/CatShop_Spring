package com.example.Cat.Shop.repository;

import com.example.Cat.Shop.model.Cart;
import com.example.Cat.Shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    List<Cart> findByUserIdAndSold (Integer userId, int sold);
    Cart findByUserIdAndProductId (Integer userId, Long productId);

    Cart findByUserIdAndProductIdAndSold (Integer userId, Long productId, int sold);

}
