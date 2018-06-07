package com.sayan.sample.genericlocationfetcher.locationfetchrelated;

public interface FetchLocationListener {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFetchFailed(String errorMessage);
}
