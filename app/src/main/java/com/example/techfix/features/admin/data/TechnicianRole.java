package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class TechnicianRole implements Serializable {
    private int id;
    private String name;

    public TechnicianRole(int id, String name) {
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
