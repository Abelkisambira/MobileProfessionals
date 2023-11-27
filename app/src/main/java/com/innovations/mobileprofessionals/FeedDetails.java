package com.innovations.mobileprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedDetails extends AppCompatActivity {

    private EditText descEditText, phoneEditText;
    private TextView locationEditText;
    private Spinner categorySpinner, categorySpinner2;
    private Button saveButton;

    // Firebase
    private FirebaseFirestore db;

    // List to store retrieved categories
    private List<Category> categoriesList = new ArrayList<>();
    private List<Subcategory> subcategoriesList = new ArrayList<>();

    public static final int LOCATION_ACTIVITY_REQUEST_CODE = 1;
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

        // Set up the spinners
        setupSpinners();

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveServiceProvider();
                Intent intent = new Intent(FeedDetails.this, Nav.class);
            }
        });

        // Retrieve categories from Firestore
        getCategoriesFromFirestore();
    }

    private void setupSpinners() {
        // Set up the Category Spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When a category is selected, update the Subcategory Spinner
                updateSubcategorySpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }



    private void getCategoriesFromFirestore() {
        // Add a listener to the Firestore collection to retrieve categories and subcategories
        db.collection("categories")
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
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(this, "Error retrieving categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FeedDetails", "Error retrieving categories", e);
                });
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

    private void updateSubcategorySpinner(int selectedCategoryPosition) {
        // Extract subcategory names based on the selected category
        Category selectedCategory = categoriesList.get(selectedCategoryPosition);

        // Log the selected category's name and position for debugging
        Log.d("FeedDetails", "Selected Category: " + selectedCategory.getName() + ", Position: " + selectedCategoryPosition);

        List<Subcategory> subcategories = selectedCategory.getSubcategories();

        // Extract subcategory names from the list
        List<String> subcategoryNames = new ArrayList<>();
        for (Subcategory subcategory : subcategories) {
            subcategoryNames.add(subcategory.getName());
        }

        // Log the size of subcategoryNames for debugging
        Log.d("FeedDetails", "Subcategory Names Size: " + subcategoryNames.size());

        // Create an ArrayAdapter using the subcategory names and a default spinner layout
        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subcategoryNames);

        // Specify the layout to use when the list of choices appears
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Subcategory Spinner
        categorySpinner2.setAdapter(subcategoryAdapter);

        // Log whether the adapter is set for debugging
        Log.d("FeedDetails", "Subcategory Spinner Adapter Set: " + (categorySpinner2.getAdapter() != null));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("latitude") && data.hasExtra("longitude")) {
                selectedLatitude = data.getDoubleExtra("latitude", 0.0);
                selectedLongitude = data.getDoubleExtra("longitude", 0.0);

                // Update UI with the latitude and longitude
                String locationText = "Latitude: " + selectedLatitude + ", Longitude: " + selectedLongitude;
                locationEditText.setText(locationText);
            }
        }
    }

}
