package com.example.administrator.location_based_services;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickToShow(View view) {
        Intent intent = new Intent(this,GoogleMapsActivity.class);
        startActivity(intent);
    }

    public void clickToGet(View view) {
        Intent intent = new Intent(this,LocationActivity.class);
        startActivity(intent);
    }
}
