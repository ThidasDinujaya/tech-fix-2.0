package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class DeviceModel implements Serializable {
    private int id;
    private int brandId;
    private String name;
    private String brandName; // Helper field for display

    public DeviceModel() {} // Required for Firestore

    public DeviceModel(int id, int brandId, String name, String brandName) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
        this.brandName = brandName;
    }

    public int getId() { return id; }
    public int getBrandId() { return brandId; }
    public String getName() { return name; }
    public String getBrandName() { return brandName; }
}
