package com.innovations.mobileprofessionals;

import static com.innovations.mobileprofessionals.FeedDetails.LOCATION_ACTIVITY_REQUEST_CODE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_ACTIVITY_REQUEST_CODE = 2;
    private static final int LOCATION_PICK_REQUEST_CODE = 3;


    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private CardView cardView;
    private TextView descriptionTextView, fullAddressTextView;
    private EditText locationDescriptionEditText, locationEditText;

    private LatLng selectedLocation;
    private Button pickHereButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI elements
        cardView = findViewById(R.id.cardView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        fullAddressTextView = findViewById(R.id.fullAddressTextView);
        locationDescriptionEditText = findViewById(R.id.locationDescriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);

        // Initialize the "Pick Here" button
        pickHereButton = findViewById(R.id.pickHereButton);
        pickHereButton.setOnClickListener(v -> {
                    // Save data and navigate to the next activity
                    saveAndNavigate();
                });

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void saveAndNavigate() {
        if (selectedLocation != null) {
            // Save data (e.g., location description) to be passed to the next activity
            String locationDescription = locationDescriptionEditText.getText().toString().trim();


            // Start the FeedDetails activity with latitude, longitude, and location description
            Intent resultintent = new Intent();

            resultintent.putExtra("latitude", selectedLocation.latitude);
            resultintent.putExtra("longitude", selectedLocation.longitude);
            resultintent.putExtra("locationDescription", locationDescription);
            setResult(RESULT_OK,resultintent);
            finish();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, refresh the map
                refreshMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up the map
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        // Get the user's last known location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            });
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Save the selected location
        selectedLocation = latLng;

        // Add a marker at the clicked location
        mMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));

        // Show the CardView with location details
        showLocationDetailsCardView();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Handle marker click event
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;

        // Save the selected location
        selectedLocation = new LatLng(latitude, longitude);

        // Show the CardView with location details
        showLocationDetailsCardView();

        return true;
    }

    private void showLocationDetailsCardView() {
        if (selectedLocation != null) {
            // Show the CardView
            cardView.setVisibility(View.VISIBLE);

            // Update UI elements with selected location details
            locationEditText.setText(String.format("%s, %s", selectedLocation.latitude, selectedLocation.longitude));

            // Set fullAddressEditText as non-editable
            locationEditText.setFocusable(false);
            locationEditText.setFocusableInTouchMode(false);
            locationEditText.setClickable(false);
        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Handle the result from the FeedDetails activity if needed
//        }
//    }

    private void refreshMap() {
        // Refresh the map when the location permission is granted
        if (mMap != null) {
            mMap.clear();
            onMapReady(mMap);
        }
    }
}