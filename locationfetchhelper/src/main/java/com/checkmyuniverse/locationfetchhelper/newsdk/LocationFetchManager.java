package com.checkmyuniverse.locationfetchhelper.newsdk;

import android.content.Context;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.checkmyuniverse.locationfetchhelper.deprecated.LocationPermissionListener;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Sayan Mukherjee <br/>
 * LocationFetchManager will help you to check & request for location permission, fetch one time current location, continuous location fetching and update
 * to get the object of this type use the below code...
 * <pre>
 * {@code
 * LocationFetchManager.getInstance(context);
 * }
 * </pre>
 */
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

    /**
     * Use this method for checking location permission
     * <p>
     *
     * @param locationRequest            the {@link LocationRequest} custom Object with the location priority for fetching location. You may pass a null argument for default implementation, this will forcefully use high accuracy permission
     * </p> <p>
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     *                                   </p>
     */
    void checkPermission(@Nullable LocationRequest locationRequest, @NonNull LocationPermissionListener locationPermissionListener);

    /**
     * Use this method for checking location permission
     * <p>
     * @param isHighAccuracy             if true, will request permission for high accuracy, else will request for balanced power
     *                                   </p>
     * <p>
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     *                                   </p>
     */
    void checkPermission(boolean isHighAccuracy, @NonNull LocationPermissionListener locationPermissionListener);

    /**
     * Use this method for checking location permission
     * <p>
     * @param locationPriority           will only take {@link LocationRequest} priority, will only work on LocationRequest.PRIORITY_HIGH_ACCURACY and LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY for now
     * </p>
     * <p>
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     * </p>
     */
    void checkPermission(int locationPriority, @NonNull LocationPermissionListener locationPermissionListener);

    /**
     * Use this method for checking location permission
     * <p>
     *
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     *                                   </p>
     */
    void checkHighAccuracyPermission(LocationPermissionListener locationPermissionListener);

    /**
     * Use this method for checking location permission
     * <p>
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     *                                   </p>
     */
    void checkBalancedPowerPermission(@NonNull LocationPermissionListener locationPermissionListener);
}
