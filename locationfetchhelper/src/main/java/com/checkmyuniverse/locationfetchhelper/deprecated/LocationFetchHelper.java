package com.checkmyuniverse.locationfetchhelper.deprecated;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * @deprecated
 * <p>
 *     Recommended use {@link com.checkmyuniverse.locationfetchhelper.newsdk.LocationFetchManager} class
 * </p>
 * <p>
 * Generic plain java class which will help to fetch location by using Activity or Service & alarm
 * manager & alarm broadcast & on boot complete broadcast etc.
 * Add the following line of code in the manifest under the application tag
 * </p><p> </br>
 * &lt;activity
 * android:name=".locationfetchrelated.LocationFetchHelper$LocationFetchActivity"
 * android:theme="@style/Theme.AppCompat.Translucent" /&gt;
 * </p>
 * <p>
 * &lt;service
 * android:name=".locationfetchrelated.LocationFetchHelper$LocationFetchService"
 * android:enabled="true"
 * android:exported="true" /&gt;
 * </p>
 * <p>
 * &lt;receiver
 * android:name=".locationfetchrelated.AlarmBootReceiver"
 * android:enabled="true"
 * android:exported="true">
 * &lt;intent-filter>
 * &lt;action android:name="android.intent.action.BOOT_COMPLETED"/&gt;
 * &lt;/intent-filter>
 * &lt;/receiver>
 * </p>
 * <p>
 * &lt;receiver
 * android:name=".locationfetchrelated.AlarmBroadcastReceiver"
 * android:enabled="true"
 * android:exported="true" /&gt;
 * </p>
 *
 */
@Deprecated
public class LocationFetchHelper {
    private Context context;

    /**
     * Use this constructor for default operations (1 minute, balanced power)
     * <p>
     *
     * @param context          use Activity context
     *                         </p><p>
     * @param mListener        listener for getting location fetch callbacks (success or failed) {@link FetchLocationSuccessListener}
     *                         </p><p>
     * @param mFailureListener failure listener for getting error message callback
     *                         </p><p>
     * @param shouldUseService if true, this helper class will be using a service and alarm manager
     *                         for fetching continuous and repeated location fetching; even after
     *                         rebooting it may continue
     *                         </p>
     */
    @Deprecated
    public LocationFetchHelper(Context context, FetchLocationSuccessListener mListener, FetchLocationFalureListener mFailureListener, boolean shouldUseService) {
        this.context = context;
        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(20 * 1000);
        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(10 * 1000);
        LocationFetchHelperSingleton.getInstance().setShouldUseService(shouldUseService);
        LocationFetchHelperSingleton.getInstance().setLocationPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        startLocationFetchActivity();
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
    @Deprecated
    public LocationFetchHelper(Context context, FetchLocationSuccessListener mListener, FetchLocationFalureListener mFailureListener, long locationIntervalTime, long locationFastestIntervalTime, int locationPriority, boolean shouldUseService) {
        this.context = context;
        LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
        LocationFetchHelperSingleton.getInstance().setFetchLocationFailureListener(mFailureListener);
        LocationFetchHelperSingleton.getInstance().setLocationIntervalTime(locationIntervalTime);
        LocationFetchHelperSingleton.getInstance().setLocationFastestIntervalTime(locationFastestIntervalTime);
        LocationFetchHelperSingleton.getInstance().setShouldUseService(shouldUseService);
        LocationFetchHelperSingleton.getInstance().setLocationPriority(locationPriority);
        startLocationFetchActivity();
    }

    /**
     * Use this constructor for default operations (1 minute, balanced power)
     * <p>
     *  @param context                    use Activity context
     *                                   </p><p>
     * @param locationRequest the {@link LocationRequest} Object with the location priority for fetching location;
     *
     * @param locationPermissionListener listener for getting location permission callbacks (success or failed) {@link LocationPermissionListener}
     */
    @Deprecated
    public LocationFetchHelper(Context context, LocationRequest locationRequest, LocationPermissionListener locationPermissionListener) {
        this.context = context;
        LocationFetchHelperSingleton.getInstance().setLocationPermissionListener(locationPermissionListener);
        LocationFetchHelperSingleton.getInstance().setIsOnlyPermissionCheck(true);
        LocationFetchHelperSingleton.getInstance().setLocationRequest(locationRequest);
        startLocationFetchActivity();
    }

    /**
     * Call this method to stop the continuous location update
     *
     * @param context Activity context
     * @return true if successfully canceled the service, false if failed
     */
    public static boolean stopLocationService(Context context) {
        Intent cancelAlarm = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent cancelAlarmPendingIntent = PendingIntent.getBroadcast(context, LocationFetchHelper.LocationFetchService.REQUEST_CODE_ALARM, cancelAlarm, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(cancelAlarmPendingIntent);
            return true;
        } else {
            return false;
        }

    }

    private void startLocationFetchActivity() {
        try {
            Activity activity = (Activity) context;
            Intent intent = new Intent(activity, LocationFetchActivity.class);
            activity.startActivity(intent);
        } catch (Exception e) {
            //this is a call from the alarm broadcast
            //start service here
            startLocationFetchService();
        }
    }

    private void startLocationFetchService() {
        LocationFetchHelperSingleton.getInstance().setContext(context);
        try {
            Intent intentService = new Intent(context, LocationFetchService.class);
            context.stopService(intentService);
            context.startService(intentService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class LocationFetchActivity extends AppCompatActivity {

        private static final int REQUEST_FOR_LOCATION = 10123;
        private static final int REQUEST_CHECK_SETTINGS = 20123;
        private FusedLocationProviderClient mFusedLocationClient;
        private LocationRequest mLocationRequest;
        private FetchLocationSuccessListener mListener;
        private FetchLocationFalureListener mfailureListener;
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
            if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()) {
                requestPermissionForLocation();
            } else {
                mListener = LocationFetchHelperSingleton.getInstance().getFetchLocationListener();
                mfailureListener = LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener();
                initializeFusedLocationProviderClient();
                createLocationRequest();
                requestPermissionForLocation();
            }
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
            if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()){
                builder.addLocationRequest(LocationFetchHelperSingleton.getInstance().getLocationRequest());
            }else {
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
                        if (LocationFetchHelperSingleton.getInstance().getIsOnlyPermissionCheck()){
                            LocationFetchHelperSingleton.getInstance().getLocationPermissionListener().onPermissionGranted();
                            finish();
                        }else {
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
                                                }else {
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
            LocationFetchHelperSingleton.getInstance().setContext(getApplicationContext());
            try {
                Intent intentService = new Intent(this, LocationFetchService.class);
                stopService(intentService);
                startService(intentService);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (LocationFetchHelperSingleton.getInstance().isShouldUseService()) {
                startLocationFetchService();
                finish();
            } else
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
                if (mfailureListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mfailureListener.onLocationFetchFailed(errorMessage);
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

    public static class LocationFetchService extends Service implements GoogleApiClient.ConnectionCallbacks {

        public static final int REQUEST_CODE_ALARM = 1011;
        private FetchLocationSuccessListener mListener;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
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


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            initiateLocationRequest();
        }

        private void initiateLocationRequest() {
            mListener = LocationFetchHelperSingleton.getInstance().getFetchLocationListener();
            initializeLocationProviderClient();
        }

        private void initializeLocationProviderClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();
            createLocationRequest();
        }

        //first call this method
        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(LocationFetchHelperSingleton.getInstance().getLocationIntervalTime());
            mLocationRequest.setFastestInterval(LocationFetchHelperSingleton.getInstance().getLocationFastestIntervalTime());
            try {
                mLocationRequest.setPriority(LocationFetchHelperSingleton.getInstance().getLocationPriority());
            } catch (IllegalArgumentException e) {
                try {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        }

        //then this method
        public void requestPermissionForLocation() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission not granted for this application", Toast.LENGTH_SHORT).show();
                // not granted, explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                            REQUEST_FOR_LOCATION);
//                } else {
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                            REQUEST_FOR_LOCATION);
//                }
            } else {
                //permission already granted
                getCurrentLocation();
            }
        }

        //fetch location
        @SuppressLint("MissingPermission")
        private void getCurrentLocation() {

            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (location != null)
                afterLocationFetchSucceed(location);
            else
                startLocationUpdates();
//                afterLocationFetchFailed("unable to fetch your current Location");
        }


        @SuppressLint("MissingPermission")
        private void startLocationUpdates() {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }

        private void afterLocationFetchFailed(String errorMessage) {
            FetchLocationFalureListener fetchLocationFailureListener = LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener();
            if (fetchLocationFailureListener != null) {
                fetchLocationFailureListener.onLocationFetchFailed(errorMessage);
            } else {
                Toast.makeText(this, errorMessage + ", please check your gps settings.", Toast.LENGTH_SHORT).show();
            }
        }

        private void afterLocationFetchSucceed(final Location location) {
            if (mListener != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLocationFetched(location.getLatitude(), location.getLongitude());
                        stopSelf();
                    }
                }, 200);
            } else if (LocationFetchHelperSingleton.getInstance().getFetchLocationListener() != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LocationFetchHelperSingleton.getInstance().getFetchLocationListener().onLocationFetched(location.getLatitude(), location.getLongitude());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        stopSelf();
                    }
                }, 200);
            }
            long locationIntervalTime = LocationFetchHelperSingleton.getInstance().getLocationIntervalTime();
            LocationFetchHelperSingleton.getInstance().setFetchLocationListener(mListener);
            setAlarm(getApplicationContext(), locationIntervalTime);
            //TODO uncomment this for send location to server using retrofit
//                sentCurrentLocationToServer(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

        }

        //TODO uncomment this for send location to server using retrofit
//        //sent location data to server
//        private void sentCurrentLocationToServer(String latitude, String longitude) {
//            Context context = LocationFetchHelperSingleton.getInstance().getContext();
//            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//            String userId = sharedPreferences.getString("userId", "");
//            InterceptorHTTPClientCreator.createInterceptorHTTPClient(getApplicationContext());
//            com.desideals.krishna.desidealsPartner.desidealssdk.Service service = new DesiDealsSdk.Builder().build(getApplicationContext()).getService();
//            service.sendLocationToServer(userId, latitude, longitude).enqueue(new Callback<LocationResponse>() {
//                @Override
//                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
//                    if (response.isSuccessful()) {
////                        if (response.body().getResult().equals("success")) {
////                            Toast.makeText(getApplicationContext(), "server", Toast.LENGTH_SHORT).show();
////                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<LocationResponse> call, Throwable t) {
//                }
//            });
//
//        }

        private static void setAlarm(Context context, long triggerAfter) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, alarmIntent, 0);
            // setRepeating() lets you specify a precise custom interval--in this case,
            // 20 minutes.
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), triggerAfter, alarmPendingIntent);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 60 * 1, alarmPendingIntent);
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_location", MODE_PRIVATE);
            sharedPreferences.edit().putLong("triggerAfter", triggerAfter);
            context.registerReceiver(new AlarmBootReceiver(), new IntentFilter("android.intent.action.BOOT_COMPLETED"));
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            requestPermissionForLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mGoogleApiClient.disconnect();
//            mListener = null;
        }
    }
}
