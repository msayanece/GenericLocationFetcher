package com.checkmyuniverse.locationfetchhelper.deprecated;

public interface LocationPermissionListener {
    void onPermissionGranted();

    void onPermissionDenied(String errorMessage);
}
