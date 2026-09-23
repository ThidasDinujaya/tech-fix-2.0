package com.example.techfix.features.admin.data;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private int id;
    private String slotName;
    private String status;
    private String branchName;

    public TimeSlot() {} // Required for Firestore

    public TimeSlot(int id, String slotName, String status) {
        this(id, slotName, status, "Colombo Main");
    }

    public TimeSlot(int id, String slotName, String status, String branchName) {
        this.id = id;
        this.slotName = slotName;
        this.status = status;
        this.branchName = branchName;
    }

    public int getId() { return id; }
    public String getSlotName() { return slotName; }
    public String getStatus() { return status; }
    public String getBranchName() { return branchName != null ? branchName : "Colombo Main"; }

    public void setId(int id) { this.id = id; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public void setStatus(String status) { this.status = status; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}
