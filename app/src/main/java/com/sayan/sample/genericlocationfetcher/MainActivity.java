package com.sayan.sample.genericlocationfetcher;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sayan.sample.genericlocationfetcher.locationfetchrelated.FetchLocationFalureListener;
import com.sayan.sample.genericlocationfetcher.locationfetchrelated.FetchLocationSuccessListener;
import com.sayan.sample.genericlocationfetcher.locationfetchrelated.LocationFetchHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fetchLocation();
        startLocationService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLocationUpdates();
            }
        }, 10000);
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
        new LocationFetchHelper(this, null, new FetchLocationFalureListener() {
            @Override
            public void onLocationFetchFailed(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, 30*60*1000, 15*60*1000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, true);
    }

    private void stopLocationUpdates(){
        if (LocationFetchHelper.stopLocationService(this)){
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "unable to stop service now, please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}