package com.sayan.sample.genericlocationfetcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationFalureListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationSuccessListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.LocationFetchHelper;
import com.checkmyuniverse.locationfetchhelper.deprecated.LocationPermissionListener;
import com.checkmyuniverse.locationfetchhelper.newsdk.LocationFetchManager;
import com.checkmyuniverse.locationfetchhelper.newsdk.LocationFetchManagerImplementer;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {
    private static final long LOCATION_FASTEST_INTERVAL = 5 * 1000;
    private static final long LOCATION_INTERVAL = 20 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fetchLocation();
//        startLocationService();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                stopLocationUpdates();
//            }
//        }, 100000);
        getLocationPermission();
    }

    private void getLocationPermission() {
//        LocationFetchManager locationFetchManager = LocationFetchManager.getInstance(this);
//        locationFetchManager.checkHighAccuracyPermission(new LocationPermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionDenied(String errorMessage) {
//                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    private void fetchLocation(){
        new LocationFetchHelper(this, new FetchLocationSuccessListener() {
            @Override
            public void onLocationFetched(double latitude, double longitude) {
                Toast.makeText(MainActivity.this, "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_LONG).show();
            }
        }, new FetchLocationFalureListener() {
            @Override
            public void onLocationFetchFailed(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, false);
    }

    private void startLocationServiceDefault(){
        new LocationFetchHelper(this, null, new FetchLocationFalureListener() {
            @Override
            public void onLocationFetchFailed(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, true);
    }

    private void startLocationService(){
        new LocationFetchHelper(this, new FetchLocationSuccessListener() {
            @Override
            public void onLocationFetched(double latitude, double longitude) {
                Toast.makeText(MainActivity.this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();
            }
        }, new FetchLocationFalureListener() {
            @Override
            public void onLocationFetchFailed(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, 30 * 60 * 1000, 15 * 60 * 1000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, true);
    }

    private void stopLocationUpdates(){
        if (LocationFetchHelper.stopLocationService(this)){
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "unable to stop service now, please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}