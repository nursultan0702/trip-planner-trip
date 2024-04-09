package com.tripplannertrip.service;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.model.PlaceRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PlaceService {
    Set<PlaceEntity> findExistingPlaces(List<Long> ids);

    List<PlaceRecord> getAllPlacesByTripId(Long tripId);

    List<PlaceEntity> getPlacesByCriteria(LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          List<String> emails,
                                          String country);

    PlaceRecord getPlaceById(Long id);

    PlaceRecord createPlace(PlaceRecord placeRecord);

    PlaceRecord updatePlace(Long id, PlaceRecord placeRecord);

    void deletePlace(Long id);
}
