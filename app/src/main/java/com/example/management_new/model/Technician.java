package com.example.management_new.model;

public class Technician {
    private int id;
    private String name;
    private String role;
    private String branchName;
    private String status;

    public Technician(int id, String name, String role, String branchName, String status) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.branchName = branchName;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getBranchName() { return branchName; }
    public String getStatus() { return status; }
}