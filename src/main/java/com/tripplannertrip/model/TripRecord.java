package com.tripplannertrip.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record TripRecord(Long tripId,
                         String userId,
                         String name,
                         String description,
                         LocalDateTime startDate,
                         LocalDateTime endDate,
                         Set<String> members,
                         List<Long> placeIds) {
}
