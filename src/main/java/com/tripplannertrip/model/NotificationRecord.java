package com.tripplannertrip.model;

import lombok.Builder;

import java.util.Set;

@Builder
public record NotificationRecord(
        TripRecord trip,
        Set<PlaceRecord> places) {
}
