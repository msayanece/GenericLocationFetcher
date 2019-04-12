package com.checkmyuniverse.locationfetchhelper.newsdk.listeners;

public interface FetchLocationListener {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFetchFailed(String errorMessage);
}
