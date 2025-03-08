package com.example.pricetracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceTrackerConfig {

	//Allow users to configure how often the price check runs
    @Value("${pricetracker.check-interval}")
    private long checkInterval;

    //Users should be able to set the percentage or absolute amount of the price drop that triggers a notification
    @Value("${pricetracker.price-drop-threshold}")
    private double priceDropThreshold;

    //choose between email, SMS, AWS topic, or a web alert
    @Value("${pricetracker.notification-method}")
    private String notificationMethod;

    public long getCheckInterval() {
        return checkInterval;
    }

    public double getPriceDropThreshold() {
        return priceDropThreshold;
    }

    public String getNotificationMethod() {
        return notificationMethod;
    }

	public void setPriceDropThreshold(double threshold) {
		priceDropThreshold = threshold;
	}

	public void setNotificationMethod(String method) {
		notificationMethod = method;
	}
}
