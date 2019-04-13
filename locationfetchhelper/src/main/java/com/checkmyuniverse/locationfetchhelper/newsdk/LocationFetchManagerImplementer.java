package com.checkmyuniverse.locationfetchhelper.newsdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationFalureListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.FetchLocationSuccessListener;
import com.checkmyuniverse.locationfetchhelper.deprecated.LocationPermissionListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * @author Sayan Mukher
 * @version 0.1
 * Generic plain java class which will help to fetch location
 */
public final class LocationFetchManagerImplementer implements LocationFetchManager {
    private static LocationFetchManager instance;
    private Context context;

    //region Initialization region
    {
        instance = this;
    }

    static LocationFetchManager getInstance() {
        return instance;
    }

    void addNewContext(Context context) {
        this.context = context;
    }
    //endregion

    LocationFetchManagerImplementer(Context context) {
        this.context = context;
    }

    //region check permission methods region
    @Override
    public void checkPermission(@Nullable LocationRequest locationRequest, @NonNull LocationPermissionListener locationPermissionListener) {
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(locationRequest);
        startLocationPermissionCheckActivity(true);
    }

    @Override
    public void checkPermission(boolean isHighAccuracy, @NonNull LocationPermissionListener locationPermissionListener) {
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
        startLocationPermissionCheckActivity(isHighAccuracy);
    }

    @Override
    public void checkPermission(int locationPriority, @NonNull LocationPermissionListener locationPermissionListener) {
        boolean isHighAccuracy = false;
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
        if (locationPriority == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            isHighAccuracy = true;
        }
        startLocationPermissionCheckActivity(isHighAccuracy);
    }

    @Override
    public void checkHighAccuracyPermission(LocationPermissionListener locationPermissionListener) {
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
        startLocationPermissionCheckActivity(true);
    }

    @Override
    public void checkBalancedPowerPermission(@NonNull LocationPermissionListener locationPermissionListener) {
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
        startLocationPermissionCheckActivity(false);
    }
    //endregion

    @Override
    public void fetchLocation(Context context, FetchLocationSuccessListener mListener, FetchLocationFalureListener mFailureListener) {
        this.context = context;
        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(20 * 1000);
        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(10 * 1000);
        LocationFetchHelperSingleton.getInstance().setLocationPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        startFetchingLocation();
    }

    private void startFetchingLocation() {
        startLocationPermissionCheckActivity(true, new LocationPermissionListener() {
            @Override
            public void onPermissionGranted() {
                startLocationFetchActivity();
            }

            @Override
            public void onPermissionDenied(String errorMessage) {
                if (LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener() != null)
                    LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener().onLocationFetchFailed(errorMessage);
            }
        });
    }

    /**
     * Use this constructor for extended operations
     * <p>
     *
     * @param context                     use Activity context
     *
     *                                    </p><p>
     * @param mListener                   listener for getting location fetch callbacks (success or failed)  {@link FetchLocationSuccessListener}
     *                                    </p><p>
     * @param mFailureListener            failure listener for getting error message callback
     *                                    </p><p>
     * @param locationIntervalTime        the interval time for fetching location
     *                                    </p><p>
     * @param locationFastestIntervalTime the fastest interval time for fetching location,
     *                                    this <b>will be ignored if param shouldUseService is true<b/>
     *                                    </p><p>
     * @param locationPriority            the location priority for fetching location;
     *                                    must be one of the<br/>
     *                                    {@link LocationRequest#PRIORITY_HIGH_ACCURACY},<br/>
     *                                    {@link LocationRequest#PRIORITY_BALANCED_POWER_ACCURACY},<br/>
     *                                    {@link LocationRequest#PRIORITY_LOW_POWER},<br/>
     *                                    {@link LocationRequest#PRIORITY_NO_POWER}<br/>
     *                                    </p><p>
     * @param shouldUseService            if true, this helper class will be using a service and alarm manager
     *                                    for fetching continuous and repeated location fetching; even after
     *                                    rebooting it may continue
     *                                    </p>
     */
    LocationFetchManagerImplementer(Context context, FetchLocationSuccessListener mListener, FetchLocationFalureListener mFailureListener, long locationIntervalTime, long locationFastestIntervalTime, int locationPriority, boolean shouldUseService) {
        this.context = context;
        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(locationIntervalTime);
        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(locationFastestIntervalTime);
        LocationFetchHelperSingleton.getInstance().setShouldUseService(shouldUseService);
        LocationFetchHelperSingleton.getInstance().setLocationPriority(locationPriority);
        startLocationFetchActivity();
    }

    private void startLocationPermissionCheckActivity(boolean isHighAccuracy) {
        try {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, LocationPermissionCheckActivity.class);
            intent.putExtra("isHighAccuracy", isHighAccuracy);
            activity.startActivity(intent);
        } catch (Exception e) {
            //impossible block
            throw new LocationFetchException("Cannot start LocationPermissionCheckActivity");
        }
    }

    private void startLocationPermissionCheckActivity(boolean isHighAccuracy, LocationPermissionListener locationPermissionListener) {
        try {
            LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
            LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, LocationPermissionCheckActivity.class);
            intent.putExtra("isHighAccuracy", isHighAccuracy);
            activity.startActivity(intent);
        } catch (Exception e) {
            //impossible block
            throw new LocationFetchException("Cannot start LocationPermissionCheckActivity");
        }
    }

    /**
     * Call this method to stop the continuous location update
     *
     * @param context Activity context
     * @return true if successfully canceled the service, false if failed
     */
    public static boolean stopLocationService(Context context) {
        return false;
    }

    private void startLocationFetchActivity() {
        try {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, LocationFetchActivity.class);
            activity.startActivity(intent);
        } catch (Exception e) {
            //impossible block
            throw new LocationFetchException("Cannot start LocationFetchActivity");
        }
    }

    public static class LocationPermissionCheckActivity extends AppCompatActivity {

        private static final long LOCATION_FASTEST_INTERVAL = 5 * 1000;
        private static final long LOCATION_INTERVAL = 20 * 1000;
        private static final int REQUEST_FOR_LOCATION = 10123;
        private static final int REQUEST_CHECK_SETTINGS = 20123;
        private boolean isHighAccuracy;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            isHighAccuracy = getIntent().getBooleanExtra("isHighAccuracy", false);
            initiateLocationRequest();
        }

        private void initiateLocationRequest() {
            requestPermissionForLocation();
        }

        protected LocationRequest createLocationRequest() {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(LOCATION_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
            if (isHighAccuracy) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            }
            return locationRequest;
        }

        //then this method
        public void requestPermissionForLocation() {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // not granted, explanation?
                Toast.makeText(this, "Initiating permission request...", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_FOR_LOCATION);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_FOR_LOCATION);
                }
            } else {
                //permission already granted
                handleLocationRequestPermissionResult();
            }
        }

        //permission granted
        private void handleLocationRequestPermissionResult() {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            if (LocationFetchHelperSingleton.getInstance().getLocationRequest() != null) {
                builder.addLocationRequest(LocationFetchHelperSingleton.getInstance().getLocationRequest());
            } else {
                builder.addLocationRequest(createLocationRequest());
            }
            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        /* requests here.
                         * All location settings are satisfied. The client can initialize location
                         */
                        if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                            finish();
                        }
                    } catch (ApiException exception) {
                        int code = exception.getStatusCode();
                        switch (code) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                /* Location settings are not satisfied. But could be fixed by showing the
                                 * user a progressDialog.
                                 */
                                try {
                                    /* Cast to a resolvable exception.
                                     */
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    /* Show the progressDialog by calling startResolutionForResult(),
                                     * and check the result in onActivityResult().
                                     */
                                    Toast.makeText(LocationPermissionCheckActivity.this, "Initiating permission request...", Toast.LENGTH_SHORT).show();
                                    resolvable.startResolutionForResult(
                                            LocationPermissionCheckActivity.this,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    /* Ignore the error.
                                     */
                                } catch (ClassCastException e) {
                                    /* Ignore, should be an impossible error.
                                     */
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                /* Location settings are not satisfied. However, we have no way to fix the
                                 * settings so we won't show the progressDialog.
                                 */
                                new AlertDialog.Builder(LocationPermissionCheckActivity.this)
                                        .setMessage("GPS is not enabled. Do you want to go to settings menu?")
                                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                                                    LocationFetchHelperSingleton.getInstance().getLocationPermissionListener()
                                                            .onPermissionDenied("Location permission denied");
                                                    finish();
                                                }
//                                                if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
//                                                    LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied("Location denied");
//                                                }else {
//                                                    afterLocationFetchFailed("Location denied");
//                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                                break;
                        }
                    }
                }
            });
        }

        //not called for now, it is not working WIP
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String permissions[], @NonNull int[] grantResults) {
            Log.d("sayan", " onrequestlocationpermission");
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
                        Log.d("sayan", " yes selected");
                        if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                            finish();
                        }
                    } else {
                        //permission denied
                        if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied("Location permission denied");
                            finish();
                        }
                    }
                    break;
                }
                case REQUEST_FOR_LOCATION: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
                        handleLocationRequestPermissionResult();
                    } else {
                        if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied("Please turn on location permission in Settings");
                            finish();
                        }
                    }
                    break;
                }
            }
        }

        //after permission and location ON, this method will be called
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS: {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                                LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                                finish();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            if (LocationFetchHelperSingleton.getInstance().getLocationPermissionListener() != null) {
                                LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied("Please turn on your location in Settings");
                                finish();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }

    }

    public static class LocationFetchActivity extends AppCompatActivity {

        private static final int REQUEST_FOR_LOCATION = 10123;
        private static final int REQUEST_CHECK_SETTINGS = 20123;
        private FusedLocationProviderClient mFusedLocationClient;
        private LocationRequest mLocationRequest;
        private FetchLocationSuccessListener mListener;
        private FetchLocationFalureListener mFailureListener;
        private boolean mRequestingLocationUpdates = false;
        private LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                afterLocationFetchSucceed(locationResult.getLastLocation());
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable()) {
                    afterLocationFetchFailed("Unable to fetch your location");
                }
            }
        };

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initiateLocationRequest();
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            if (mRequestingLocationUpdates) {
                stopLocationUpdates();
            }
        }

        private void initiateLocationRequest() {
            mListener = LocationFetchHelperSingleton.getInstance().getFetchLocationListener();
            mFailureListener = LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener();
            initializeFusedLocationProviderClient();
            createLocationRequest();
            getCurrentLocation();
        }

        private void initializeFusedLocationProviderClient() {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        //first call this method
        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(LocationFetchHelperSingleton.getInstance().getLocationIntervalTime());
            mLocationRequest.setFastestInterval(LocationFetchHelperSingleton.getInstance().getLocationFastestIntervalTime());
            mLocationRequest.setPriority(LocationFetchHelperSingleton.getInstance().getLocationPriority());
        }

        //then this method
        public void requestPermissionForLocation() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // not granted, explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_FOR_LOCATION);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_FOR_LOCATION);
                }
            } else {
                //permission already granted
                handleLocationRequestPermission();
            }
        }

        //permission granted
        private void handleLocationRequestPermission() {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                builder.addLocationRequest(LocationFetchHelperSingleton.getInstance().getLocationRequest());
            } else {
                builder.addLocationRequest(mLocationRequest);
            }
            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // requests here.
                        // All location settings are satisfied. The client can initialize location
                        if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                            finish();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getCurrentLocation();
                                }
                            }, 3000);
                        }
                    } catch (ApiException exception) {
                        int code = exception.getStatusCode();
                        switch (code) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a progressDialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the progressDialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            LocationFetchActivity.this,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the progressDialog.
                                new android.app.AlertDialog.Builder(LocationFetchActivity.this)
                                        .setMessage("GPS is not enabled. Do you want to go to settings menu?")
                                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                                                    LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied("Location denied");
                                                } else {
                                                    afterLocationFetchFailed("Location denied");
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                                break;
                        }
                    }
                }
            });
        }

        private void startLocationFetchService() {

        }

        @SuppressLint("MissingPermission")
        private void startLocationUpdates() {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper() /* Looper */);
            mRequestingLocationUpdates = true;
        }

        private void stopLocationUpdates() {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mRequestingLocationUpdates = true;
        }

        //fetch location
        @SuppressLint("MissingPermission")
        private void getCurrentLocation() {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new MyOnSuccessListener());
        }

        //location listener
        private class MyOnSuccessListener implements OnSuccessListener<Location> {
            private int mOnSuccessCallCounter;

            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                //location fetch successful
                mOnSuccessCallCounter++;
                if (location != null) {
                    //able to get current location
                    afterLocationFetchSucceed(location);
                } else {
                    //location returned is null
                    if (mOnSuccessCallCounter <= 5) {
                        //try another time
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(LocationFetchActivity.this, this);
                    } else {
                        startLocationUpdates();
                    }
                }
            }
        }

        //not called for now, it is not working WIP
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String permissions[], @NonNull int[] grantResults) {
            Log.d("sayan", " onrequestlocationpermission");
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
                        Log.d("sayan", " yes selected");
                        if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                            finish();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getCurrentLocation();
                                }
                            }, 3000);
                        }
                    } else {
                        //permission denied
                        afterLocationFetchFailed("Location permission denied");
                    }
                    break;
                }
                case REQUEST_FOR_LOCATION: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
//                    Toast.makeText(getApplicationContext(), "SMS Permission granted", Toast.LENGTH_LONG).show();
                        handleLocationRequestPermission();
                    } else {
                        afterLocationFetchFailed("Please turn on location permission in Settings");
                    }
                    break;
                }
            }
        }

        //after permission and location ON, this method will be called
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS: {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
//                            Toast.makeText(this, "Location ON", Toast.LENGTH_SHORT).show();
                            if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                                LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                                finish();
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getCurrentLocation();
                                    }
                                }, 3000);
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
//                            Toast.makeText(this, "Location OFF", Toast.LENGTH_SHORT).show();
                            afterLocationFetchFailed("Please turn on your location in Settings");
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }

        private void afterLocationFetchFailed(final String errorMessage) {
            if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionDenied(errorMessage);
                finish();
            } else {
                if (mFailureListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFailureListener.onLocationFetchFailed(errorMessage);
                        }
                    }, 200);
                }
                finish();
            }
        }

        private void afterLocationFetchSucceed(final Location location) {
            if (mListener != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLocationFetched(location.getLatitude(), location.getLongitude());
                    }
                }, 200);
            }
            finish();
        }

    }

}
