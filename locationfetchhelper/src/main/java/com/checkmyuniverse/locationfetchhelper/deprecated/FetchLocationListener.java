package com.checkmyuniverse.locationfetchhelper.deprecated;

public interface FetchLocationListener {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFetchFailed(String errorMessage);
}
