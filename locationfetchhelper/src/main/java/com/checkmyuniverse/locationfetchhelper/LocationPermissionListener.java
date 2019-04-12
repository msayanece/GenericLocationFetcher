package com.checkmyuniverse.locationfetchhelper;

public interface LocationPermissionListener {
    void onPermissionGranted();

    void onPermissionDenied(String errorMessage);
}
