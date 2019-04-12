package com.checkmyuniverse.locationfetchhelper.newsdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkmyuniverse.locationfetchhelper.deprecated.LocationPermissionListener;
import com.google.android.gms.location.LocationRequest;

public interface LocationFetchManager {

    void checkPermission(@Nullable LocationRequest locationRequest, @NonNull LocationPermissionListener locationPermissionListener);

    void checkPermission(boolean isHighAccuracy, @NonNull LocationPermissionListener locationPermissionListener);

    void checkPermission(int locationPriority, @NonNull LocationPermissionListener locationPermissionListener);

    void checkHighAccuracyPermission(LocationPermissionListener locationPermissionListener);

    void checkBalancedPowerPermission(@NonNull LocationPermissionListener locationPermissionListener);
}
