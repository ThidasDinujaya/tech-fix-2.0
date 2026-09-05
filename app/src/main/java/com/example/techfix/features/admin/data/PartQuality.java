package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class PartQuality implements Serializable {
    private int id;
    private String name;
    private double multiplier;

    public PartQuality(int id, String name, double multiplier) {
        this.id = id;
        this.name = name;
        this.multiplier = multiplier;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getMultiplier() { return multiplier; }

    @Override
    public String toString() {
        return name;
    }
}
