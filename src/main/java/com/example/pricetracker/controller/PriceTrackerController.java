package com.example.pricetracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricetracker.config.PriceTrackerConfig;
import com.example.pricetracker.model.ProductPrice;
import com.example.pricetracker.repository.ProductPriceRepository;
import com.example.pricetracker.service.PriceTrackerService;

@RestController
@RequestMapping("/api")
public class PriceTrackerController {

	@Autowired
    private final ProductPriceRepository priceRepository;
    @Autowired
    private PriceTrackerService priceTrackerService;
    @Autowired
    private PriceTrackerConfig priceTrackerConfig;

    public PriceTrackerController(ProductPriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("/prices/history")
    public List<ProductPrice> getPriceHistory() {
        return priceRepository.findAll();
    }

    @GetMapping("/prices/check")
    public String checkPriceManually() {
        return "Price tracking service is running!";
    }
    
    @GetMapping("/prices/current")
    public double getCurrentPrice() {
        return priceTrackerService.scrapeLatestPrice();  // Scrape and return the current price
    }
    
    @GetMapping("/threshold")
    public double getThreshold() {
        return priceTrackerConfig.getPriceDropThreshold();  // Return the current threshold
    }

    @PostMapping("/threshold")
    public String setThreshold(@RequestBody double threshold) {
        priceTrackerConfig.setPriceDropThreshold(threshold);  // Set new threshold
        return "Price drop threshold set to: " + threshold;
    }
    
    @GetMapping("/notification-method")
    public String getNotificationMethod() {
        return priceTrackerConfig.getNotificationMethod();  // Return current notification method
    }

    @PostMapping("/notification-method")
    public String setNotificationMethod(@RequestBody String method) {
        priceTrackerConfig.setNotificationMethod(method);  // Set new notification method
        return "Notification method set to: " + method;
    }
}
