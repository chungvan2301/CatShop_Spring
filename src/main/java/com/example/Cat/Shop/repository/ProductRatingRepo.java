package com.example.Cat.Shop.repository;

import com.example.Cat.Shop.model.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRatingRepo extends JpaRepository<ProductRating, Long> {
    List <ProductRating> findAllByProductIdOrderByDateTimeDesc(Long productId);

    ProductRating findByProductIdAndUserId(Long productId, Integer userId);
    void deleteByProductIdAndUserId(Long productId, Integer userId);
}
