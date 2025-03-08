package com.example.pricetracker.repository;

import com.example.pricetracker.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    ProductPrice findTopByProductNameOrderByCheckedAtDesc(String productName);
}
