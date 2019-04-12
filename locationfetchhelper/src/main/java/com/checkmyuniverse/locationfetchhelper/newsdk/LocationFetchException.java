package com.checkmyuniverse.locationfetchhelper.newsdk;

public class LocationFetchException extends RuntimeException {

    public LocationFetchException() {
        super("Some undetected error occurred during fetching location");
    }

    public LocationFetchException(String message) {
        super(message);
    }
}
