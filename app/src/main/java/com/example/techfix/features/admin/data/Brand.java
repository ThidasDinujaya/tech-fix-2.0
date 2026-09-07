package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class Brand implements Serializable {
    private int id;
    private String name;
    private String category;

    public Brand() {} // Required for Firestore

    public Brand(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return name;
    }
}
