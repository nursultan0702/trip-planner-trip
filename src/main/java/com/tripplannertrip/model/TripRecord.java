package com.tripplannertrip.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TripRecord(String name,
                         String description,
                         LocalDateTime startDate,
                         LocalDateTime endDate,
                         List<Long> members,
                         List<Long> placeIds) {
}
