package com.checkmyuniverse.locationfetchhelper.newsdk.listeners;

public interface LocationPermissionListener {
    void onPermissionGranted();

    void onPermissionDenied(String errorMessage);
}
