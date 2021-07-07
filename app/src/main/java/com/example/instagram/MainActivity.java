package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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