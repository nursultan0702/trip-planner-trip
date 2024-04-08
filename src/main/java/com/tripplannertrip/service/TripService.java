package com.tripplannertrip.service;

import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import java.time.LocalDateTime;
import java.util.List;

public interface TripService {
  TripRecord createTrip(TripRecord tripRecord);

  TripRecord getById(long id);

  TripEntity getEntityById(long id);

  List<TripRecord> getTrips(
      LocalDateTime startDate,
      LocalDateTime endDate,
      List<String> emails,
      DateSortType sort,
      int page,
      int limit);

  TripRecord updateTrip(long tripId, TripRecord tripRecord);

  void deleteTrip(long tripId);
}
