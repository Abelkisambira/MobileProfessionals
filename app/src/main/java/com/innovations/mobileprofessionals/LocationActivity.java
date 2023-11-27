package com.innovations.mobileprofessionals;

import static com.innovations.mobileprofessionals.FeedDetails.LOCATION_ACTIVITY_REQUEST_CODE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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

    // Add this variable to store the selected location
    private LatLng selectedLocation;

    @Override
    public void onMapClick(LatLng latLng) {
        // Save the selected location
        selectedLocation = latLng;

        // Add a marker at the clicked location
        mMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Handle marker click event
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;

        // Save the selected location
        selectedLocation = new LatLng(latitude, longitude);

        // Start the FeedDetails activity with latitude and longitude
        Intent intent = new Intent(LocationActivity.this, FeedDetails.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);

        return true;
    }

    // Add this method to get the selected location in the LocationActivity
    public LatLng getSelectedLocation() {
        return selectedLocation;
    }



    private void refreshMap() {
        // Refresh the map when the location permission is granted
        if (mMap != null) {
            mMap.clear();
            onMapReady(mMap);
        }
    }
}
