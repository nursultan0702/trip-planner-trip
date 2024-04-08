package com.tripplannertrip.model;

import lombok.Builder;

import java.util.List;

@Builder
public record PlaceRecord(Long id,
                          String country,
                          String city,
                          String name,
                          List<String> images) {
}
