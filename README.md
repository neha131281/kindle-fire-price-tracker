# Automated Price Drop Notification for Kindle Fire

## Project Overview

This application monitors the price of the Kindle Fire on Amazon.com and sends an automated notification whenever there's a price drop. The notification method can be chosen from available options, such as email, SMS, or web notification. The price monitoring runs periodically, and you can configure the threshold for price drop notifications.

## Features

- **Web Scraping**: The application scrapes the price of Kindle Fire from Amazon.com.
- **Price Comparison**: Compares the latest price with the previously recorded price to detect price drops.
- **Notification**: Sends notifications (email, SMS, or web) when a price drop is detected.
- **Logging**: Logs all price checks and notifications sent.
- **Configurable Parameters**: Set the price drop threshold and notification method via API endpoints.

---

## API Endpoints

### 1. Get Price History
**Endpoint:**  
`GET /api/prices/history`  

**Description:**  
Retrieves the price history of the Kindle Fire.

**Response:**  
A list of all saved price records.

---

### 2. Check Service Status
**Endpoint:**  
`GET /api/prices/check`  

**Description:**  
Checks if the price tracking service is running.

**Response:**  
Returns a simple message indicating the status of the service.

---

### 3. Get Current Price
**Endpoint:**  
`GET /api/prices/current`  

**Description:**  
Fetches the current price of the Kindle Fire by scraping the Amazon product page.

**Response:**  
Returns the latest price.

---

### 4. Get Price Drop Threshold
**Endpoint:**  
`GET /api/threshold`  

**Description:**  
Gets the current price drop threshold value.

**Response:**  
Returns the current threshold value.

---

### 5. Set Price Drop Threshold
**Endpoint:**  
`POST /api/threshold`  

**Description:**  
Sets a new price drop threshold. The threshold is the amount or percentage by which the price must drop to trigger a notification.

**Request Body:**  
```json
{
  "threshold": 10.0
}

**Response:**
returns price drop threshold set to: 10.0

---

### 6. Get Price Drop Threshold
**Endpoint:**  
`GET /api/notification-method`  

**Description:**
Gets the current notification method (email, SMS, or web).

**Response:**
Returns the current notification method.

---

### 7. Set Notification Method
**Endpoint:**  
`POST /api/notification-method`  

**Description:**  
Sets the notification method (email, SMS, or web).

**Request Body:**  
```json
{
  "method": "email"
}

**Response:**
Notification method set to: email

## How to Run the Application

### Prerequisites

- Java 11 or later
- Maven
- Spring Boot
- MySQL Database
- Jsoup

### Steps

1. **Clone this repository**  
   Open a terminal or command prompt and run:  
   ```bash
   git clone https://github.com/neha131281/kindle-fire-price-tracker.git
2. **Navigate to the project directory**
   ```bash
   cd price-tracker
3. **Build the project using Maven**
   ```bash
   mvn clean install
4. **Run the application**
   ```bash
   mvn spring-boot:run
5. **Access the application**
   open your browser and go to: http://localhost:8080/check
   
### Challenges Faced and Solutions
1. Web Scraping Limitations
 Problem: Amazon's dynamic structure and bot detection made scraping difficult.
 Solution: Used Jsoup for scraping, identify the price UI element and adjusted user-agent headers to mimic a browser request.
2. Database Management
 Problem: Storing and retrieving prices efficiently.
 Solution: Implemented indexing on productName and used findTopByProductNameOrderByCheckedAtDesc to fetch the latest price.
3. Testing and Debugging
 Problem: Ensuring that price drop detection works correctly.
 Solution: Wrote unit tests for PriceTrackerService.
### Additional Features Implemented
 Configurable Notification Settings: Users can set thresholds for price drop alerts.
 Scheduled Price Checks: Uses @Scheduled annotation to automate price tracking.
 Logging Mechanism: Records every price check and notification event.
### Future Enhancements
 Graphical Price History Visualization
 Enhance configurations for notification systems example: SMS, AWS Topic etc
 Multiple Product Tracking – currently tracks 1 hardcoded product


