package com.checkmyuniverse.locationfetchhelper.deprecated;

/**
 * Location fetch listener for getting location callbacks
 */
public interface FetchLocationFalureListener {
    /**
     * this method will be called after a failed fetch of location
     * @param errorMessage the reason of failure
     */
    void onLocationFetchFailed(String errorMessage);
}
