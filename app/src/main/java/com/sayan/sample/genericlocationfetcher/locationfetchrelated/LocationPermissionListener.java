package com.sayan.sample.genericlocationfetcher.locationfetchrelated;

public interface LocationPermissionListener {
    void onPermissionGranted();

    void onPermissionDenied(String errorMessage);
}
