package com.example.techfix.features.branches.data;

public class Technician {
    private int id;
    private String name;
    private String branchName;
    private String status;

    public Technician(int id, String name, String branchName, String status) {
        this.id = id;
        this.name = name;
        this.branchName = branchName;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBranchName() { return branchName; }
    public String getStatus() { return status; }
}
