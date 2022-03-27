package com.example.locationupdateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //The distance from previous position required for location update to occur
    public static final double UPDATE_DISTANCE = 10;
    //The interval in milliseconds that the location will update
    public static final int INTERVAL = 2500;

    //The current distance from previous position as a multiple of UPDATE_DISTANCE (rounded up)
    private double notifyThreshold;
    public static final String NOTIFY_THRESHOLD_KEY = "notifyThresholdKey";

    //The user's initial position
    private Location previousPosition;
    public static final String PREVIOUS_POSITION_KEY = "previousPositionKey";

    //Reference to the text display
    private TextView textDisplay;
    public static final String CURRENT_DISPLAY_TEXT = "currentDisplayText";

    //Fused location provider
    private FusedLocationProviderClient fusedLocationProviderClient;
    //Location for current location
    private Location mCurrentLocation;
    public static final String M_CURRENT_LOCATION_KEY = "mCurrentLocationKey";
    //Location Request
    private LocationRequest locationRequest;
    //Location Callback
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign View and button
        textDisplay = findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreviousPosition();
            }
        });

        //fusedLocation assign
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check location settings
        createLocationRequest();

        //assign locationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                updateCurrentLocation();
            }
        };
    }


    private void updateUI(double distance) {
        String textToDisplay = getResources().getString(R.string.movementMessagePart1) + " " +
                String.format(getResources().getString(R.string.defaultDoubleFormat),distance) +
                " " + getResources().getString(R.string.movementMessagePart2);
        //if (distance > notifyThreshold) display text
        if(distance > notifyThreshold || distance < (notifyThreshold - UPDATE_DISTANCE)){
            textDisplay.setText(textToDisplay);
        }
        //handle threshold changes
        updateNotifyThreshold(distance);
    }

    private void updateNotifyThreshold(double distance){
        //set notifyThreshold to distance rounded to nearest multiple of UPDATE_DISTANCE
        notifyThreshold = UPDATE_DISTANCE*Math.ceil(Math.abs(distance/UPDATE_DISTANCE));
        //handle 0 event
        if (notifyThreshold == 0){
            notifyThreshold = UPDATE_DISTANCE;
        }
    }

    private void processNewLocation(){
        if(previousPosition != null){
            double distanceFromPrevious = mCurrentLocation.distanceTo(previousPosition);
            updateUI(distanceFromPrevious);
            //testing toast
//            Toast.makeText(MainActivity.this,
//                    String.format(getResources().getString(R.string.defaultDoubleFormat),
//                            distanceFromPrevious) + " metres from previous location...",
//                    Toast.LENGTH_SHORT).show();
        }else{
            //testing toast
//            Toast.makeText(MainActivity.this, "location updating...", Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().
                addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //testing toast
//                Toast.makeText(MainActivity.this, "SETTINGS ARE ENOUGH :)", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //testing toast
//                Toast.makeText(MainActivity.this, "SETTINGS NOT ENOUGH :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions();
        }else{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.getMainLooper());
        }

    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void updateCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions();

        } else {
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken())
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mCurrentLocation = location;
                                processNewLocation();
                            }
                        }
                    });
        }
    }

    private void setPreviousPosition() {
        notifyThreshold = UPDATE_DISTANCE;
        previousPosition = mCurrentLocation;
        if (previousPosition != null) {
            Toast.makeText(this, getResources().getString(R.string.positionResetMessage), Toast.LENGTH_SHORT).show();
            textDisplay.setText(getResources().getString(R.string.nearPreviousPositionMessage));
        } else {
            //position reset fail message
            Toast.makeText(this, getResources().getString(R.string.positionResetFailMessage), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions(){
        //request permissions
        List<String> wantedPermissions = new ArrayList<>();
        wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, wantedPermissions.toArray(new String[wantedPermissions.size()]), 0);
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(NOTIFY_THRESHOLD_KEY, notifyThreshold);
        outState.putParcelable(PREVIOUS_POSITION_KEY, previousPosition);
        outState.putParcelable(M_CURRENT_LOCATION_KEY, mCurrentLocation);

        String currentTextViewState = textDisplay.getText().toString();
        outState.putString(CURRENT_DISPLAY_TEXT, currentTextViewState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        notifyThreshold = savedInstanceState.getDouble(NOTIFY_THRESHOLD_KEY);
        previousPosition = savedInstanceState.getParcelable(PREVIOUS_POSITION_KEY);
        mCurrentLocation = savedInstanceState.getParcelable(M_CURRENT_LOCATION_KEY);
        textDisplay.setText(savedInstanceState.getString(CURRENT_DISPLAY_TEXT));
    }
}