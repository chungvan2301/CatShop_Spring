package com.example.Cat.Shop.repository;

import com.example.Cat.Shop.model.Address;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepo extends JpaRepository <Address, Long> {

    List<Address> findByUserIdOrderByAddressDefaultDesc (Integer userId);

    Optional <Address> findByUserIdAndAddressDefault (Integer userId, Boolean addressDefault);


}
