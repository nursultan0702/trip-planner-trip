package com.tripplannertrip.service;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.model.PlaceRecord;
import java.util.List;
import java.util.Set;

public interface PlaceService {
  Set<PlaceEntity> findExistingPlaces(List<Long> ids);

  List<PlaceRecord> getAllPlacesByTripId(Long tripId);

  PlaceRecord getPlaceById(Long id);

  PlaceRecord createPlace(PlaceRecord placeRecord);

  PlaceRecord updatePlace(Long id, PlaceRecord placeRecord);

  void deletePlace(Long id);
}
