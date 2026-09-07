package com.example.techfix.features.branches.data;

import java.io.Serializable;

public class TechAvailability implements Serializable {
    private int id;
    private int technicianId;
    private String availableDate;
    private int isAvailable;

    public TechAvailability() {} // Required for Firestore

    public TechAvailability(int id, int technicianId, String availableDate, int isAvailable) {
        this.id = id;
        this.technicianId = technicianId;
        this.availableDate = availableDate;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }
    public int getTechnicianId() { return technicianId; }
    public String getAvailableDate() { return availableDate; }
    public int getIsAvailable() { return isAvailable; }
}
