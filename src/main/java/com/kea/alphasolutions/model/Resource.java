package com.kea.alphasolutions.model;

public class Resource {
    private int resourceId;
    private String name;
    private String role;
    private String systemRole;
    private String username;
    private String password;

    // Getters & Setters
    public int getResourceId() {
        return resourceId;
    }
    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getSystemRole() {
        return systemRole;
    }
    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
