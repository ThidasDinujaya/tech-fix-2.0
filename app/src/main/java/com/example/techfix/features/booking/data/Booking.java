package com.example.techfix.features.booking.data;

import java.io.Serializable;

// Represents a repair booking request made by a customer
public class Booking implements Serializable {
    private int id;
    private int serviceId;
    private String deviceType;
    private String brand;
    private String model;
    private String description;
    private String appointmentDate;
    private String appointmentTime;
    private String imagePath;
    private String status;
    private int userId;
    private String branchName;
    private String technicianName;

    public Booking() {} // Required for Firebase

    public Booking(int id, int serviceId, String deviceType, String brand, String model, 
                   String description, String appointmentDate, String imagePath, String status, 
                   int userId, String branchName, String technicianName) {
        this(id, serviceId, deviceType, brand, model, description, appointmentDate, "", imagePath, status, userId, branchName, technicianName);
    }

    public Booking(int id, int serviceId, String deviceType, String brand, String model, 
                   String description, String appointmentDate, String appointmentTime, String imagePath, 
                   String status, int userId, String branchName, String technicianName) {
        this.id = id;
        this.serviceId = serviceId;
        this.deviceType = deviceType;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.imagePath = imagePath;
        this.status = status;
        this.userId = userId;
        this.branchName = branchName;
        this.technicianName = technicianName;
    }

    // Getters
    public int getId() { return id; }
    public int getServiceId() { return serviceId; }
    public String getDeviceType() { return deviceType; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getDescription() { return description; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime != null ? appointmentTime : ""; }
    public String getImagePath() { return imagePath; }
    public String getStatus() { return status; }
    public int getUserId() { return userId; }
    public String getBranchName() { return branchName; }
    public String getTechnicianName() { return technicianName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setStatus(String status) { this.status = status; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }
}
