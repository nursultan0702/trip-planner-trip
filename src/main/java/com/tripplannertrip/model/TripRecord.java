package com.tripplannertrip.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TripRecord(String tripName, String tripDescription,
                         LocalDateTime tripStartDate, LocalDateTime tripEndDate,
                         String tripStatus, String country, List<Long> members){
}
