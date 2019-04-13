package com.checkmyuniverse.locationfetchhelper.newsdk;

import android.content.Context;

import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.FetchLocationFailureListener;
import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.FetchLocationSuccessListener;
import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.LocationPermissionListener;
import com.google.android.gms.location.LocationRequest;

public class LocationFetchHelperSingleton {

    private static LocationFetchHelperSingleton instance;
    private int locationPriority;
    private long locationIntervalTime;
    private long locationFastestIntervalTime;
    private boolean shouldUseService;
    private FetchLocationSuccessListener fetchLocationListener;
    private FetchLocationFailureListener fetchLocationFailureListener;
    private Context context;
    private LocationPermissionListener locationPermissionListener;
    private boolean isOnlyPermissionCheck;
    private LocationRequest locationRequest;


    private LocationFetchHelperSingleton() {
    }

    static LocationFetchHelperSingleton getInstance() {
        if (instance == null) {
            instance = new LocationFetchHelperSingleton();
        }
        return instance;
    }

    int getLocationPriority() {
        return locationPriority;
    }

    void setLocationPriority(int locationPriority) {
        this.locationPriority = locationPriority;
    }

    long getLocationIntervalTime() {
        return locationIntervalTime;
    }

    void setLocationIntervalTime(long locationIntervalTime) {
        this.locationIntervalTime = locationIntervalTime;
    }

    long getLocationFastestIntervalTime() {
        return locationFastestIntervalTime;
    }

    void setLocationFastestIntervalTime(long locationFastestIntervalTime) {
        this.locationFastestIntervalTime = locationFastestIntervalTime;
    }

    FetchLocationSuccessListener getFetchLocationListener() {
        return fetchLocationListener;
    }

    void setFetchLocationListener(FetchLocationSuccessListener fetchLocationListener) {
        this.fetchLocationListener = fetchLocationListener;
    }


    public Context getContext() {
        return context;
    }

    void setContext(Context context) {
        this.context = context;
    }

    boolean isShouldUseService() {
        return shouldUseService;
    }

    void setShouldUseService(boolean shouldUseService) {
        this.shouldUseService = shouldUseService;
    }

    FetchLocationFailureListener getFetchLocationFailureListener() {
        return fetchLocationFailureListener;
    }

    void setFetchLocationFailureListener(FetchLocationFailureListener fetchLocationFailureListener) {
        this.fetchLocationFailureListener = fetchLocationFailureListener;
    }

    void setLocationPermissionListener(LocationPermissionListener locationPermissionListener) {
        this.locationPermissionListener = locationPermissionListener;
    }

    LocationPermissionListener getLocationPermissionListener() {
        return locationPermissionListener;
    }

    void setIsOnlyPermissionCheck(boolean isOnlyPermissionCheck) {
        this.isOnlyPermissionCheck = isOnlyPermissionCheck;
    }

    boolean getIsOnlyPermissionCheck() {
        return isOnlyPermissionCheck;
    }

    void setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }

    LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
