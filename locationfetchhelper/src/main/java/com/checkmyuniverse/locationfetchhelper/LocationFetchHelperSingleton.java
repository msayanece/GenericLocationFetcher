package com.checkmyuniverse.locationfetchhelper;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;

public class LocationFetchHelperSingleton {

    private static LocationFetchHelperSingleton instance;
    private int locationPriority;
    private long locationIntervalTime;
    private long locationFastestIntervalTime;
    private boolean shouldUseService;
    private FetchLocationSuccessListener fetchLocationListener;
    private FetchLocationFalureListener fetchLocationFailureListener;
    private Context context;
    private LocationPermissionListener locationPermissionListener;
    private boolean isOnlyPermissionCheck;
    private LocationRequest locationRequest;


    private LocationFetchHelperSingleton() {
    }

    public static LocationFetchHelperSingleton getInstance() {
        if (instance == null) {
            instance = new LocationFetchHelperSingleton();
        }
        return instance;
    }

    public int getLocationPriority() {
        return locationPriority;
    }

    public void setLocationPriority(int locationPriority) {
        this.locationPriority = locationPriority;
    }

    public long getLocationIntervalTime() {
        return locationIntervalTime;
    }

    public void setLocationIntervalTime(long locationIntervalTime) {
        this.locationIntervalTime = locationIntervalTime;
    }

    public long getLocationFastestIntervalTime() {
        return locationFastestIntervalTime;
    }

    public void setLocationFastestIntervalTime(long locationFastestIntervalTime) {
        this.locationFastestIntervalTime = locationFastestIntervalTime;
    }

    public FetchLocationSuccessListener getFetchLocationListener() {
        return fetchLocationListener;
    }

    public void setFetchLocationListener(FetchLocationSuccessListener fetchLocationListener) {
        this.fetchLocationListener = fetchLocationListener;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isShouldUseService() {
        return shouldUseService;
    }

    public void setShouldUseService(boolean shouldUseService) {
        this.shouldUseService = shouldUseService;
    }

    public FetchLocationFalureListener getFetchLocationFailureListener() {
        return fetchLocationFailureListener;
    }

    public void setFetchLocationFailureListener(FetchLocationFalureListener fetchLocationFailureListener) {
        this.fetchLocationFailureListener = fetchLocationFailureListener;
    }

    public void setLocationPermissionListener(LocationPermissionListener locationPermissionListener) {
        this.locationPermissionListener = locationPermissionListener;
    }

    public LocationPermissionListener getLocationPermissionListener() {
        return locationPermissionListener;
    }

    public void setIsOnlyPermissionCheck(boolean isOnlyPermissionCheck) {
        this.isOnlyPermissionCheck = isOnlyPermissionCheck;
    }

    public boolean getIsOnlyPermissionCheck() {
        return isOnlyPermissionCheck;
    }

    public void setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
