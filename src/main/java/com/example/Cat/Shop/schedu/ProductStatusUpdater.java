package com.example.Cat.Shop.schedu;

import com.example.Cat.Shop.model.Product;
import com.example.Cat.Shop.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
public class ProductStatusUpdater {
    @Autowired
    private ProductRepo productRepo;

    @Scheduled(fixedRate = 60000*60*24)
    public void updateProductStatus() {
        List<Product> products = productRepo.findAll();
        for (Product product : products) {
            if (ChronoUnit.DAYS.between(product.getAddDate(), LocalDateTime.now()) > 7) {
                product.setProductStatus("");
                productRepo.save(product);
            }
        }
    }

}
