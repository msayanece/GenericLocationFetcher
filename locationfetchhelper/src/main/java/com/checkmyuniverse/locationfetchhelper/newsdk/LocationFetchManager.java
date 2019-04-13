package com.checkmyuniverse.locationfetchhelper.newsdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkmyuniverse.locationfetchhelper.deprecated.LocationPermissionListener;
import com.google.android.gms.location.LocationRequest;

import java.net.ContentHandler;

public interface LocationFetchManager {

    static LocationFetchManager getInstance(Context context){
        LocationFetchManager instance = LocationFetchManagerImplementer.getInstance();
        if (instance == null){
            return new LocationFetchManagerImplementer(context);
        }else {
            ((LocationFetchManagerImplementer)instance).addNewContext(context);
            return instance;
        }
    }

    void checkPermission(@Nullable LocationRequest locationRequest, @NonNull LocationPermissionListener locationPermissionListener);

    void checkPermission(boolean isHighAccuracy, @NonNull LocationPermissionListener locationPermissionListener);

    void checkPermission(int locationPriority, @NonNull LocationPermissionListener locationPermissionListener);

    void checkHighAccuracyPermission(LocationPermissionListener locationPermissionListener);

    void checkBalancedPowerPermission(@NonNull LocationPermissionListener locationPermissionListener);
}
