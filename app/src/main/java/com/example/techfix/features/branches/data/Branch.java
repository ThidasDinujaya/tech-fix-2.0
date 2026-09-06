package com.example.techfix.features.branches.data;

import java.io.Serializable;

public class Branch implements Serializable {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String phone2;
    private String hoursMonFri;
    private String hoursSat;
    private String hoursSun;
    private String mapLink;

    public Branch(int id, String name, String address, String phone, String phone2, 
                  String hoursMonFri, String hoursSat, String hoursSun, 
                  String mapLink) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.phone2 = phone2;
        this.hoursMonFri = hoursMonFri;
        this.hoursSat = hoursSat;
        this.hoursSun = hoursSun;
        this.mapLink = mapLink;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getPhone2() { return phone2; }
    public String getHoursMonFri() { return hoursMonFri; }
    public String getHoursSat() { return hoursSat; }
    public String getHoursSun() { return hoursSun; }
    public String getMapLink() { return mapLink; }
}
