package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.PostsFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigation;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // For a FragmentManager, you make replace certain parts of the screen based on transactions
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.actionHome:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_LONG).show();
                        fragment = new PostsFragment();
                        break;
                    case R.id.actionCompose:
                        Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_LONG).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.actionProfile:
                    default:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_LONG).show();
                        fragment = new ProfileFragment();
                        break;
                }
                // Replace the FrameLayout with the fragment and commit it
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigation.setSelectedItemId(R.id.actionHome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu (adds items to the action bar if it's present)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Must return true for the menu to be displayed
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log out
        if (item.getItemId() == R.id.miLogout) {
            ParseUser.logOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}