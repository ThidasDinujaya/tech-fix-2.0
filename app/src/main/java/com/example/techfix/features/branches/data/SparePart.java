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
    private String branchName;

    public SparePart() {} // Required for Firestore

    public SparePart(int id, String name, int stock, double price, String brand, String model, String quality, String category) {
        this(id, name, stock, price, brand, model, quality, category, "Colombo Main");
    }

    public SparePart(int id, String name, int stock, double price, String brand, String model, String quality, String category, String branchName) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.brand = brand;
        this.model = model;
        this.quality = quality;
        this.category = category;
        this.branchName = branchName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getQuality() { return quality; }
    public String getCategory() { return category; }
    public String getBranchName() { return branchName != null ? branchName : ""; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStock(int stock) { this.stock = stock; }
    public void setPrice(double price) { this.price = price; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setCategory(String category) { this.category = category; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}
