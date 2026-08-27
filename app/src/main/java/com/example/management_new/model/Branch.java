package com.example.management_new.model;

public class Branch {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String hours;
    private double latitude;
    private double longitude;

    public Branch(int id, String name, String address, String phone, String hours, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.hours = hours;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getHours() { return hours; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}