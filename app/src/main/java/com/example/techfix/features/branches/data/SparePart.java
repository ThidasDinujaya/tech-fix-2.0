package com.example.techfix.features.branches.data;

import java.io.Serializable;

public class SparePart implements Serializable {
    private int id;
    private String name;
    private int stock;
    private double price;
    private String brand;
    private String model;
    private String quality;
    private String category;

    public SparePart() {} // Required for Firestore

    public SparePart(int id, String name, int stock, double price, String brand, String model, String quality, String category) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.brand = brand;
        this.model = model;
        this.quality = quality;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getQuality() { return quality; }
    public String getCategory() { return category; }
}
