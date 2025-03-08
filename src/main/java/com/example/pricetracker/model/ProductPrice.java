package com.example.pricetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price")
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private double currentPrice;
    private double previousPrice;
    private LocalDateTime checkedAt;

    public ProductPrice() {
        this.checkedAt = LocalDateTime.now();
    }
    
    // Constructor to set product name and price
    public ProductPrice(String productName, double currentPrice) {
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.checkedAt = LocalDateTime.now();  //timestamp of the price check
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getPreviousPrice() { return previousPrice; }
    public void setPreviousPrice(double previousPrice) { this.previousPrice = previousPrice; }

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}
