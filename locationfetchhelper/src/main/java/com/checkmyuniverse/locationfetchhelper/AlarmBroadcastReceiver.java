package com.checkmyuniverse.locationfetchhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final int REQUEST_CODE_ALARM = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        FetchLocationSuccessListener listener = LocationFetchHelperSingleton.getInstance().getFetchLocationListener();
        FetchLocationFalureListener failureListener = LocationFetchHelperSingleton.getInstance().getFetchLocationFailureListener();
        long locationIntervalTime = LocationFetchHelperSingleton.getInstance().getLocationIntervalTime();
        long fastestIntervalTime = LocationFetchHelperSingleton.getInstance().getLocationFastestIntervalTime();
        int locationPriority = LocationFetchHelperSingleton.getInstance().getLocationPriority();
        if (locationIntervalTime == 0) {
            new LocationFetchHelper(context, listener, failureListener, true);
        }else {
            new LocationFetchHelper(context, listener, failureListener, locationIntervalTime, fastestIntervalTime, locationPriority, true);
        }
    }
}