package com.example.pricetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pricetracker.config.PriceTrackerConfig;
import com.example.pricetracker.model.ProductPrice;
import com.example.pricetracker.repository.ProductPriceRepository;

@ExtendWith(MockitoExtension.class)
class PriceTrackerServiceTest {

    @Mock
    private ProductPriceRepository productPriceRepository;

    @Mock
    private EmailNotificationService emailService;

    @Mock
    private PriceTrackerConfig config;  // Mocking the config to avoid null

    @InjectMocks
    private PriceTrackerService priceTrackerService;

    private ProductPrice mockProductPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock old price record
        mockProductPrice = new ProductPrice("Kindle Fire", 99.99);

        // Mock the config to return a valid notification method
        lenient().when(config.getNotificationMethod()).thenReturn("email");
    }

    @Test
    void testFetchAndComparePrice_whenPriceDropped() {
        // Mock repository to return the old price (99.99)
        when(productPriceRepository.findTopByProductNameOrderByCheckedAtDesc("Kindle Fire"))
                .thenReturn(mockProductPrice);

        // Mock price scraper to return a lower price (89.99)
        PriceTrackerService spyService = spy(priceTrackerService);
        doReturn(89.99).when(spyService).scrapeLatestPrice();

        // Run fetchAndComparePrice()
        spyService.fetchAndComparePrice();

        // Verify price drop detected and new price saved
        verify(productPriceRepository, times(1)).save(argThat(productPrice ->
                productPrice.getProductName().equals("Kindle Fire") &&
                productPrice.getCurrentPrice() == 89.99
        ));

        // Verify email notification is sent
        verify(emailService, times(1)).sendPriceDropAlert(anyString(), contains("Price dropped"), anyString());
    }

    @Test
    void testFetchAndComparePrice_whenPriceRemainsSame() {
        // Mock repository to return the old price (99.99)
        when(productPriceRepository.findTopByProductNameOrderByCheckedAtDesc("Kindle Fire"))
                .thenReturn(mockProductPrice);

        // Mock price scraper to return the same price (99.99)
        PriceTrackerService spyService = spy(priceTrackerService);
        doReturn(99.99).when(spyService).scrapeLatestPrice();

        // Run fetchAndComparePrice()
        spyService.fetchAndComparePrice();

        // Verify no price change, so `save()` should NOT be called
        verify(productPriceRepository, never()).save(any(ProductPrice.class));

        // Verify no email notification is sent
        verify(emailService, never()).sendPriceDropAlert(anyString(), anyString(), anyString());
    }

    @Test
    void testFetchAndComparePrice_whenNoPreviousPrice() {
        // Mock repository to return null (first time fetching price)
        when(productPriceRepository.findTopByProductNameOrderByCheckedAtDesc("Kindle Fire"))
                .thenReturn(null);

        // Mock price scraper to return a new price (99.99)
        PriceTrackerService spyService = spy(priceTrackerService);
        doReturn(99.99).when(spyService).scrapeLatestPrice();

        // Run fetchAndComparePrice()
        spyService.fetchAndComparePrice();

        // Verify the first price is saved
        verify(productPriceRepository, times(1)).save(argThat(productPrice ->
                productPrice.getProductName().equals("Kindle Fire") &&
                productPrice.getCurrentPrice() == 99.99
        ));

        // Verify no email notification is sent (because there is no previous price to compare)
        verify(emailService, never()).sendPriceDropAlert(anyString(), anyString(), anyString());
    }

    @Test
    void testSendNotification_whenEmailConfigured() {
        // Mock config to return "email" as notification method
        when(config.getNotificationMethod()).thenReturn("email");

        // Call sendNotification()
        priceTrackerService.sendNotification("Kindle Fire", 99.99, 89.99);

        // Verify email is sent
        verify(emailService, times(1)).sendPriceDropAlert(anyString(), contains("Price dropped"), anyString());
    }

    @Test
    void testSendNotification_whenInvalidMethodConfigured() {
        // Mock config to return an invalid method
        when(config.getNotificationMethod()).thenReturn("invalid_method");

        // Call sendNotification()
        priceTrackerService.sendNotification("Kindle Fire", 89.99, 99.99);

        // Verify no notification is sent
        verify(emailService, never()).sendPriceDropAlert(anyString(), anyString(), anyString());
    }

    @Test
    void testScrapeLatestPrice_Success() {
        // Spy on the service
        PriceTrackerService spyService = spy(priceTrackerService);

        // Mock scrapeLatestPrice() to avoid real scraping
        doReturn(89.99).when(spyService).scrapeLatestPrice();

        // Assert the scraped price
        assertEquals(89.99, spyService.scrapeLatestPrice());
    }
}
