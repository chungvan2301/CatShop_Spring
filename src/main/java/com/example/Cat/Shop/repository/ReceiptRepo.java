package com.example.Cat.Shop.repository;

import com.example.Cat.Shop.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepo extends JpaRepository <Receipt, Long> {
    Optional<Receipt> findByReceiptCode (String code);
    List<Receipt> findByUserIdOrderByDateCreatedDateFormatDesc (Integer userId);
}
