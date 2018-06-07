package com.sayan.sample.genericlocationfetcher.locationfetchrelated;

/**
 * Location fetch listener for getting location callbacks
 */
public interface FetchLocationSuccessListener {
    /**
     * this method will be called after successful fetch of location
     * @param latitude latitude of fetched location
     * @param longitude longitude of fetched location
     */
    void onLocationFetched(double latitude, double longitude);
}
