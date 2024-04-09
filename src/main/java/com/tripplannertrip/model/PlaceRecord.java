package com.tripplannertrip.model;

import java.util.List;
import lombok.Builder;

@Builder
public record PlaceRecord(Long tripId,
                          String country,
                          String city,
                          String name,
                          Boolean visited,
                          List<String> images) {
}
