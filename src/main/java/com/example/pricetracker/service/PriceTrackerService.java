package com.example.pricetracker.service;

import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.pricetracker.config.PriceTrackerConfig;
import com.example.pricetracker.model.ProductPrice;
import com.example.pricetracker.repository.ProductPriceRepository;

@Service
public class PriceTrackerService {

    private static final Logger logger = Logger.getLogger(PriceTrackerService.class.getName());
    final String AMAZON_KINDLE_URL = "https://www.amazon.com/Fire-HD-8-Tablet-Black-32GB/dp/B0CVDN4QS6"; 

    @Autowired
    private ProductPriceRepository productPriceRepository;
    
    @Autowired
    private EmailNotificationService emailService;

    @Autowired
    private PriceTrackerConfig config; // Inject configs

    @Scheduled(fixedRateString = "${pricetracker.check-interval}")
    public void fetchAndComparePrice() {
        // Fetch the latest price record for the product
        ProductPrice latestPrice = productPriceRepository.findTopByProductNameOrderByCheckedAtDesc("Kindle Fire");

        // Check if a price record is available
        if (latestPrice != null) {
            double currentPrice = scrapeLatestPrice();  // this method fetches the current price from Amazon
            double previousPrice = latestPrice.getCurrentPrice();

            // Compare the current and previous price
            if (currentPrice < previousPrice) {
            	logger.info("Price drop detected! Previous Price: " + previousPrice + ", New Price: " + currentPrice);

                // Save the new price record
                ProductPrice newPrice = new ProductPrice("Kindle Fire", currentPrice);
                productPriceRepository.save(newPrice);

                // Send a notification
                sendNotification("Kindle Fire", currentPrice, previousPrice);  // Your method to send notifications
            } else {
            	logger.info("No price drop detected. Current Price: " + currentPrice + ", Previous Price: " + previousPrice);
            }
        } else {
            // If no previous price is available, create the first price entry
        	logger.info("No previous price record found. Saving current price: " + scrapeLatestPrice());
            ProductPrice newPrice = new ProductPrice("Kindle Fire", scrapeLatestPrice());
            productPriceRepository.save(newPrice);
        }
    }


    // This method scrapes the price of Kindle Fire from Amazon
    public double scrapeLatestPrice() {
        try {
            // Fetch the HTML page from Amazon
			Document document = Jsoup.connect(AMAZON_KINDLE_URL).userAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
					.timeout(30 * 1000) // 10 seconds timeout
					.get();

			String fullPrice = "0";
            // Select the element that contains the price (check the page structure)
            // Example: The price might be within a span with class "a-price-whole"
            Element wholePriceElement = document.select("span.a-price-whole").first();
            Element fractionPriceElement = document.select("span.a-price-fraction").first();

            if (wholePriceElement != null && fractionPriceElement != null) {
            	fullPrice = wholePriceElement.text() + fractionPriceElement.text();
                logger.info("Full Price: " + fullPrice);
                return Double.parseDouble(fullPrice);  // Convert it to a double
            } else {
                throw new RuntimeException("Could not find the price on the page.");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception for debugging
        throw new RuntimeException("Error while scraping the price: " + e.getMessage());
	    }
	}

    void sendNotification(String productName, double oldPrice, double newPrice) {
        switch (config.getNotificationMethod().toLowerCase()) {
            case "email":
                sendEmailNotification(productName, oldPrice, newPrice);
                break;
            case "sms":
                sendSmsNotification(productName, oldPrice, newPrice);
                break;
            case "web":
                sendWebNotification(productName, oldPrice, newPrice);
                break;
            default:
                logger.warning("Invalid notification method configured.");
        }
    }

    private void sendEmailNotification(String productName, double oldPrice, double newPrice) {
    	String subject = "Price dropped for " + productName + " from $" + oldPrice + " to $" + newPrice;
    	String text = "GOOD NEWS!!! Price dropped for " + productName + " Check it out: https://www.amazon.com/Fire-HD-8-Tablet-Black-32GB/dp/B0CVDN4QS6";
    	emailService.sendPriceDropAlert("neha1312@gmail.com", subject, text);
        logger.info("Sending Email: Price dropped for " + productName + " from $" + oldPrice + " to $" + newPrice);
    }

    private void sendSmsNotification(String productName, double oldPrice, double newPrice) {
        logger.info("Sending SMS: Price dropped for " + productName + " from $" + oldPrice + " to $" + newPrice);
    }

    private void sendWebNotification(String productName, double oldPrice, double newPrice) {
        logger.info("Showing Web Alert: Price dropped for " + productName + " from $" + oldPrice + " to $" + newPrice);
    }
}
