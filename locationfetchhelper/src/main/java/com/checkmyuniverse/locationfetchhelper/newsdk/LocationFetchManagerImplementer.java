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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.FetchLocationFailureListener;
import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.FetchLocationSuccessListener;
import com.checkmyuniverse.locationfetchhelper.newsdk.listeners.LocationPermissionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

    //region Location fetch region
    @Override
    public void fetchLocation(final FetchLocationSuccessListener mListener, final FetchLocationFailureListener mFailureListener) {
        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(2000);
        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(1000);
        LocationFetchHelperSingleton.getInstance().setLocationPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startFetchingLocation();
    }

    @Override
    public void fetchLocationContinuously(/*final FetchLocationSuccessListener mListener, final FetchLocationFailureListener mFailureListener*/) {
//        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
//        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
//        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(2000);
//        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(1000);
//        LocationFetchHelperSingleton.getInstance().setLocationPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create a Constraints object that defines when the task should run

        int locationIntervalTime = 10;
        enqueueOneTimeWork(locationIntervalTime);
    }

    @Override
    public void stopCurrentLocationService() {
        WorkManager.getInstance().cancelAllWorkByTag("LOC");
    }

    @Override
    public void openHelpActivity() {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }

    /**
     * Enqueue a one time work request with work manager with initial delay
     * @param locationIntervalTime the initial delay for this work in seconds
     */
    private static void enqueueOneTimeWork(int locationIntervalTime) {
        OneTimeWorkRequest locationFetchWorker =
                new OneTimeWorkRequest.Builder(LocationFetchWorker.class)
                        .setInitialDelay(locationIntervalTime, TimeUnit.SECONDS)
                        .addTag("LOC")
                        .build();
        WorkManager.getInstance().enqueue(locationFetchWorker);
    }
    //endregion

    private void startFetchingLocation() {
        startLocationPermissionCheckActivity(new LocationPermissionListener() {
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
    LocationFetchManagerImplementer(Context context, FetchLocationSuccessListener mListener, FetchLocationFailureListener mFailureListener, long locationIntervalTime, long locationFastestIntervalTime, int locationPriority, boolean shouldUseService) {
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

    private void startLocationPermissionCheckActivity(LocationPermissionListener locationPermissionListener) {
        try {
            LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
            LocationFetchHelperSingleton.getInstance().setLocationRequest(null);
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, LocationPermissionCheckActivity.class);
            intent.putExtra("isHighAccuracy", true);
            activity.startActivity(intent);
        } catch (Exception e) {
            //impossible block
            throw new LocationFetchException("Cannot start LocationPermissionCheckActivity");
        }
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

    public static class LocationFetchActivityOld extends AppCompatActivity {

        private FusedLocationProviderClient mFusedLocationClient;
        private LocationRequest mLocationRequest;
        private FetchLocationSuccessListener mListener;
        private FetchLocationFailureListener mFailureListener;
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
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(LocationFetchActivityOld.this, this);
                    } else {
                        startLocationUpdates();
                    }
                }
            }
        }

        private void afterLocationFetchFailed(final String errorMessage) {
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

        private void afterLocationFetchSucceed(final Location location) {
            if (mListener != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLocationFetched(location);
                    }
                }, 200);
            }
            finish();
        }

    }

    public static class LocationFetchActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

        private int locationFetchCounter = 0;
        private LocationRequest mLocationRequest;
        private FetchLocationSuccessListener mListener;
        private FetchLocationFailureListener mFailureListener;
        private boolean mRequestingLocationUpdates = false;
        private GoogleApiClient mGoogleApiClient;

        //override method if you are using LocationListener. Else, simple method for next work
        @SuppressLint("MissingPermission")
        public void onLocationChanged(Location location) {
            if (locationFetchCounter < 2){
                locationFetchCounter++;
            }else {
                afterLocationFetchSucceed(location);
                new Handler().postDelayed(this::stopLocationUpdates, 150);
            }
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setUpGoogleApiClient();
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
            try {
                if (mGoogleApiClient.isConnected()) {
                    if (mRequestingLocationUpdates) {
                        stopLocationUpdates();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void initiateLocationRequest() {
            mListener = LocationFetchHelperSingleton.getInstance().getFetchLocationListener();
            mFailureListener = LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener();
            createLocationRequest();
            getCurrentLocation();
        }

        private void setUpGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        //first call this method
        protected void createLocationRequest() {
            try {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(LocationFetchHelperSingleton.getInstance().getLocationIntervalTime());
                mLocationRequest.setFastestInterval(LocationFetchHelperSingleton.getInstance().getLocationFastestIntervalTime());
                mLocationRequest.setPriority(LocationFetchHelperSingleton.getInstance().getLocationPriority());
            } catch (Exception e) {
                e.printStackTrace();
                throw new LocationFetchException("LocationRequest creation error");
            }
        }

        @SuppressLint("MissingPermission")
        private void startLocationUpdates() {
            /*uncomment this line of code if you want to use onLocationChange of LocationListener class.
             * also uncomment the implements interface of LocationListener in this activity
             */
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        private void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        //fetch location
        @SuppressLint("MissingPermission")
        private void getCurrentLocation() {
            startLocationUpdates();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            initiateLocationRequest();
        }

        @Override
        public void onConnectionSuspended(int i) {
            afterLocationFetchFailed("Could not connect to google location service");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            afterLocationFetchFailed("Could not connect to google location service");
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
                new Handler().postDelayed(() -> mListener.onLocationFetched(location), 200);
            }
            stopLocationUpdates();
            finish();
        }

    }

    public static class LocationFetchWorker extends Worker {

        public LocationFetchWorker(
                @NonNull Context context,
                @NonNull WorkerParameters params) {
            super(context, params);
        }

        @Override
        public Result doWork() {
            // Do the work here
            Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            mMainThreadHandler.post(() -> Toast.makeText(getApplicationContext(), "doWork", Toast.LENGTH_SHORT).show());
            enqueueOneTimeWork(30);

            // Indicate whether the task finished successfully with the Result
            return Result.success();
        }
    }

}
