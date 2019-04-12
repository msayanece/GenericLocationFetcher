package com.checkmyuniverse.locationfetchhelper;

public interface FetchLocationListener {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFetchFailed(String errorMessage);
}
