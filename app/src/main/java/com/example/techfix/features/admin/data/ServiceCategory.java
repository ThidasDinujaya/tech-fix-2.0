package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class ServiceCategory implements Serializable {
    private int id;
    private String name;

    public ServiceCategory() {} // Required for Firestore

    public ServiceCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
