package com.example.locationupdateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //The distance from previous position required for location update to occur
    public static final double UPDATE_DISTANCE = 10;

    //The current distance from previous position as a multiple of UPDATE_DISTANCE
    private int notifyThreshold;

    //The user's initial position
    private String previousPosition;

    //Reference to the text display
    private TextView textDisplay;

    //Reference to the button
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}