package com.sayan.sample.genericlocationfetcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationFalureListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationSuccessListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.LocationFetchHelper;
import com.checkmyuniverse.locationfetchhelper.newsdk.LocationFetchManager;
import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.LocationPermissionListener;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

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

//        fetchLocation();
        getLocationPermission();
    }

    private void getLocationPermission() {
        LocationFetchManager.getInstance(this)
                .checkHighAccuracyPermission(new LocationPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(MainActivity.this, "Granted.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Denied:=> " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    private void fetchLocation(){
        LocationFetchManager.getInstance(this)
                .fetchLocation(location -> Toast.makeText(MainActivity.this, "Location:=> " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show(),
                        errorMessage -> Toast.makeText(MainActivity.this, "Failed:=> " + errorMessage, Toast.LENGTH_SHORT).show());
    }

    private void startLocationServiceDefault(){
        LocationFetchManager.getInstance(this).fetchLocationContinuously();
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

    public void startFetching(View view) {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
        }
        startLocationServiceDefault();
    }

    public void stopFetching(View view) {
        stopLocationServiceDefault();
    }

    private void stopLocationServiceDefault() {
        LocationFetchManager.getInstance(this).stopCurrentLocationService();
    }

    public void help(View view) {
        LocationFetchManager.getInstance(this).openHelpActivity();
    }
}