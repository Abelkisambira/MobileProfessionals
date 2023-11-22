package com.innovations.mobileprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.innovations.mobileprofessionals.model.Category;
//import com.innovations.mobileprofessionals.model.ServiceProviders;
//import com.innovations.mobileprofessionals.model.Subcategory;

import java.util.ArrayList;
import java.util.List;

public class FeedDetails extends AppCompatActivity {

    private EditText descEditText, phoneEditText, locationEditText;
    private Spinner categorySpinner, categorySpinner2;
    private Button saveButton;

    // Firebase
    private FirebaseFirestore db;

    // List to store retrieved categories
    private List<Category> categoriesList = new ArrayList<>();
    private List<Subcategory> subcategoriesList = new ArrayList<>();

    private static final int LOCATION_ACTIVITY_REQUEST_CODE = 1;
    private double selectedLatitude, selectedLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_details);

        // Initialize UI elements
        descEditText = findViewById(R.id.desc);
        categorySpinner = findViewById(R.id.categorySpinner);
        categorySpinner2 = findViewById(R.id.categorySpinner2);
        phoneEditText = findViewById(R.id.phoneholder);
        saveButton = findViewById(R.id.service);
        locationEditText = findViewById(R.id.location);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveServiceProvider();
            }
        });

        // Retrieve categories from Firestore
        getCategoriesFromFirestore();
    }

    private void getCategoriesFromFirestore() {
        FirebaseFirestore.getInstance().collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear existing categories and subcategories
                    categoriesList.clear();
                    subcategoriesList.clear();

                    // Populate the categoriesList and subcategoriesList with the retrieved data
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        categoriesList.add(category);

                        // Add subcategories to the list
                        subcategoriesList.addAll(category.getSubcategories());
                    }

                    // Populate the Category Spinner with category names
                    populateCategorySpinner();

                    // Populate the Subcategory Spinner with subcategory names
                    populateSubcategorySpinner();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(this, "Error retrieving categories", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateSubcategorySpinner() {
        // Extract subcategory names from the subcategoriesList
        List<String> subcategoryNames = new ArrayList<>();
        for (Subcategory subcategory : subcategoriesList) {
            subcategoryNames.add(subcategory.getName());
        }

        // Create an ArrayAdapter using the subcategory names and a default spinner layout
        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subcategoryNames);

        // Specify the layout to use when the list of choices appears
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Subcategory Spinner
        categorySpinner2.setAdapter(subcategoryAdapter);
    }

    private void populateCategorySpinner() {
        try {
            // Extract category names from the categoriesList
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categoriesList) {
                if (category != null && category.getName() != null) {
                    categoryNames.add(category.getName());
                }
            }

            // Create an ArrayAdapter using the category names and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            categorySpinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error populating category spinner", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveServiceProvider() {
        // Get data from UI elements
        String desc = descEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString().trim();
        String speciality = categorySpinner2.getSelectedItem().toString().trim();
        String phoneNumber = phoneEditText.getText().toString().trim();

        // Validate data
        if (desc.isEmpty() || category.isEmpty() || speciality.isEmpty() || phoneNumber.isEmpty()) {
            // Display an error message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number (assuming it should be numeric and have a specific length)
        if (!phoneNumber.matches("\\d{9}")) {
            // Display an error message if the phone number is not valid
            Toast.makeText(this, "Please enter a valid 9-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new service provider object
        ServiceProviders serviceProvider = new ServiceProviders(desc, category, speciality, phoneNumber, selectedLatitude, selectedLongitude);

        // Add the service provider to Firestore
        db.collection("service_providers")
                .add(serviceProvider)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Service provider saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving service provider", Toast.LENGTH_SHORT).show();
                });
    }

    // Add onActivityResult method to handle the result from LocationActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("latitude") && data.hasExtra("longitude")) {
                selectedLatitude = data.getDoubleExtra("latitude", 0.0);
                selectedLongitude = data.getDoubleExtra("longitude", 0.0);

                // Update locationEditText with selected location
                String locationText = "Latitude: " + selectedLatitude + ", Longitude: " + selectedLongitude;
                locationEditText.setText(locationText);
            }
        }
    }
}
