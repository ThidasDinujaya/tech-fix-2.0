package com.example.techfix.features.services.data;

import java.io.Serializable;

// Data model representing a repair service
public class Service implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private String warranty;
    private String imageUrl;
    private String category; // Added category field

    // Initializes the service with all required details
    public Service(int id, String name, String description, double price, String warranty, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.warranty = warranty;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getWarranty() { return warranty; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setWarranty(String warranty) { this.warranty = warranty; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
}
