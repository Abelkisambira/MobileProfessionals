package com.innovations.mobileprofessionals;

// Subcategory.java
public class Subcategory {
    private String name;

    public Subcategory() {
        // Required empty public constructor for Firestore
    }

    public Subcategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
