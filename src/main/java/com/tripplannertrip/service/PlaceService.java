package com.tripplannertrip.service;

import com.tripplannertrip.model.PlaceRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PlaceService {

  List<PlaceRecord> getAllPlacesByTripId(Long tripId);

  List<PlaceRecord> getPlacesByCriteria(LocalDateTime startDate,
                                        LocalDateTime endDate,
                                        Set<String> emails,
                                        String country,
                                        int page,
                                        int limit);

  PlaceRecord getPlaceById(Long id);

  PlaceRecord createPlace(PlaceRecord placeRecord);

  PlaceRecord updatePlace(Long id, PlaceRecord placeRecord);

  void deletePlace(Long id);
}
