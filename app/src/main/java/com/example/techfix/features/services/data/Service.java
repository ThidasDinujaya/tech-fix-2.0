package com.example.techfix.features.services.data;

import java.io.Serializable;

// Data model representing a repair service with associated device specifications and parts
public class Service implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private String warranty;
    private String imageUrl;
    private String category;
    private String brand;
    private String model;
    private String quality;
    private int sparePartId;

    public Service() {} // Required for Firestore

    public Service(int id, String name, String description, double price, String warranty, 
                   String imageUrl, String category, String brand, String model, 
                   String quality, int sparePartId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.warranty = warranty;
        this.imageUrl = imageUrl;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.quality = quality;
        this.sparePartId = sparePartId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getWarranty() { return warranty; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getQuality() { return quality; }
    public int getSparePartId() { return sparePartId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setWarranty(String warranty) { this.warranty = warranty; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setSparePartId(int sparePartId) { this.sparePartId = sparePartId; }
}
