package com.tripplannertrip.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(long id) {
        super("Trip not found with id " + id);
    }
}
