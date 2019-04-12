package com.checkmyuniverse.locationfetchhelper.deprecated;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReceiver extends BroadcastReceiver {

    private static final long INTERVAL_TIME = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            setAlarm(context, INTERVAL_TIME);
        }
    }

    private static void setAlarm(Context context, long triggerAfter) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, LocationFetchHelper.LocationFetchService.REQUEST_CODE_ALARM, alarmIntent, 0);
        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), triggerAfter, alarmPendingIntent);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 60 * 1, alarmPendingIntent);
        }
    }
}
