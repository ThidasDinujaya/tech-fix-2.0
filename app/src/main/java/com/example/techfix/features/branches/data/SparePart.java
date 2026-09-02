package com.example.techfix.features.branches.data;

public class SparePart {
    private int id;
    private String name;
    private int stock;
    private double price;

    public SparePart(int id, String name, int stock, double price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
}
