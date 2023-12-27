package com.innovations.mobileprofessionals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Nav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new Home());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.home) {
                replaceFragment(new Home());
            } else if (itemID == R.id.book) {
                replaceFragment(new MyBookings());
            } else if (itemID == R.id.account) {
                replaceFragment(new Accoount());
            }
            else if (itemID == R.id.chats) {
                replaceFragment(new EmployerListFragment());
            }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.bottom_nav_fragment_container, fragment)
                .commit();
    }
}