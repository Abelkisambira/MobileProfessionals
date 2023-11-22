
package com.innovations.mobileprofessionals;
import java.util.ArrayList;
import java.util.List;

// Category.java
import java.util.List;

public class Category {
    private String name;
    private List<Subcategory> subcategories;

    public Category() {
        // Required empty public constructor for Firestore
    }

    public Category(String name, List<Subcategory> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Subcategory> getSubcategories() {
        if (subcategories == null) {
            return new ArrayList<>(); // Initialize subcategories to an empty list if null
        }
        return subcategories;
    }

    public void setSubcategories(List<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }
}

