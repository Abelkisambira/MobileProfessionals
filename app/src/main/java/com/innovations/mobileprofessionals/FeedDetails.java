package com.innovations.mobileprofessionals;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedDetails extends AppCompatActivity {

    private EditText descEditText, locationEditText;
    private Spinner categorySpinner;
    private LinearLayout checkboxLayout;
    private CountryCodePicker format;
    private FirebaseFirestore db;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<Category> categoriesList = new ArrayList<>();
    private Uri selectedImageUri;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final int LOCATION_ACTIVITY_REQUEST_CODE = 2;
    private double selectedLatitude, selectedLongitude;
    private String locationDescription;
    // Request code for picking a location
    private static final int LOCATION_PICK_REQUEST_CODE = 3;
    private static final int IMAGE_PICK_REQUEST_CODE = 4;
    private ImageView imageView;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_details);

        descEditText = findViewById(R.id.desc);
        categorySpinner = findViewById(R.id.categorySpinner);
        checkboxLayout = findViewById(R.id.checkBoxLayout);
        locationEditText = findViewById(R.id.location);
        imageView = findViewById(R.id.imageView);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                Intent intent = new Intent(FeedDetails.this, Login.class);
                startActivity(intent);

            }
        });

        db = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // Initialize the pick location button
        findViewById(R.id.pickLocationButton).setOnClickListener(v -> {
            // Start the LocationActivity to pick a location
            Intent intent = new Intent(FeedDetails.this, LocationActivity.class);
            startActivityForResult(intent, LOCATION_PICK_REQUEST_CODE);
        });

        // Initialize the upload image button
        findViewById(R.id.uploadButton).setOnClickListener(v -> pickImage());

        setupSpinners();
        getCategoriesFromFirestore();
        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupSpinners() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSubcategoryCheckboxes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void getCategoriesFromFirestore() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoriesList.clear();
                    List<Task<?>> tasks = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Category category = document.toObject(Category.class);
                        categoriesList.add(category);

                        Task<List<Subcategory>> subcategoryTask = db.collection("categories").document(category.getName())
                                .collection("subcategories")
                                .get()
                                .continueWith(subcategoryTaskContinuation -> {
                                    List<Subcategory> subcategoriesList = new ArrayList<>();
                                    for (QueryDocumentSnapshot subcategoryDocument : subcategoryTaskContinuation.getResult()) {
                                        Subcategory subcategory = subcategoryDocument.toObject(Subcategory.class);
                                        subcategoriesList.add(subcategory);
                                    }
                                    category.setSubcategories(subcategoriesList);
                                    return subcategoriesList;
                                });

                        tasks.add(subcategoryTask);
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(subcategoriesLists -> {
                                // All subcategory tasks completed successfully
                                // Now, you can call populateCategorySpinner()
                                populateCategorySpinner();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(this, "Error retrieving subcategories", Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void populateCategorySpinner() {
        try {
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categoriesList) {
                if (category != null && category.getName() != null) {
                    categoryNames.add(category.getName());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error populating category spinner", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSubcategoryCheckboxes(int selectedCategoryPosition) {
        Category selectedCategory = categoriesList.get(selectedCategoryPosition);

        List<Subcategory> subcategories = selectedCategory.getSubcategories();

        checkboxLayout.removeAllViews();

        for (Subcategory subcategory : subcategories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(subcategory.getName());
            checkboxLayout.addView(checkBox);
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
    }
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Professionals").child(uid);


    private void saveServiceProvider(String imageUrl, String desc, String selectedCategory, List<String> selectedSubcategories,String selectedLocation) {
        // Create a reference to the user's node in the Realtime Database based on the UID


        // Add/update the necessary details in the user's node
        Map<String, Object> professionalData = new HashMap<>();
        professionalData.put("imageUrl", imageUrl);
        professionalData.put("desc", desc);
        professionalData.put("category", selectedCategory);
        professionalData.put("subcategories", selectedSubcategories);
        professionalData.put("Location",selectedLocation);

        userRef.updateChildren(professionalData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // The data has been successfully updated in the user's node
                        Toast.makeText(this, "Service provider details saved successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FeedDetails.this, Login.class);
                        startActivity(intent);
                    } else {
                        // Handle the failure
                        Toast.makeText(this, "Error saving service provider details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadImage() {
        if (selectedImageUri != null) {
            if (user != null ) {


                // Create a unique filename based on the timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());
                String imageName = "image_" + timestamp + ".jpg";
                imageName = imageName.replace(".", ",");
                // Create a reference to the image URL using the dynamic filename
                DatabaseReference imageUrlRef = userRef.child(imageName);

                // Upload the image URL to Realtime Database
                imageUrlRef.setValue(selectedImageUri.toString())
                        .addOnSuccessListener(aVoid -> {
                            // Image URL uploaded successfully
                            String imageUrl = String.valueOf(selectedImageUri);
                            // You can save the imageUrl or display it in an ImageView
                            // For example:
                            Glide.with(this).load(imageUrl).into(imageView);

                            // Now, save the imageUrl along with other details in the Realtime Database
                            saveServiceProvider(imageUrl, descEditText.getText().toString().trim(),
                                    categorySpinner.getSelectedItem().toString().trim(),
                                    getSelectedSubcategories(),locationEditText.getText().toString().trim());
                        })
                        .addOnFailureListener(e -> {
                            // Handle the failure to upload image URL
                            Log.e("FeedDetails", "Error uploading image URL", e);
                            Toast.makeText(this, "Error uploading image URL", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Handle the case where the user is not authenticated
                Log.d("UploadImage", "User not authenticated");
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    // Helper method to get the selected subcategories
    private List<String> getSelectedSubcategories() {
        List<String> selectedSubcategories = new ArrayList<>();
        for (int i = 0; i < checkboxLayout.getChildCount(); i++) {
            View view = checkboxLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    selectedSubcategories.add(checkBox.getText().toString());
                }
            }
        }
        return selectedSubcategories;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve data from the result intent
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);
            locationDescription = data.getStringExtra("locationDescription");

            // Update the UI with the selected location details
            locationEditText.setText(locationDescription);
        }

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // You can load and display the image using Glide if needed
                Glide.with(this).load(selectedImageUri).into(imageView);
            } else {
                Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Prepare the result data
        super.onBackPressed();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", selectedLatitude);
        resultIntent.putExtra("longitude", selectedLongitude);
        resultIntent.putExtra("locationDescription", locationDescription);

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}