package com.tripplannertrip.service;

import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface TripService {
    TripRecord createTrip(TripRecord tripRecord);

    TripRecord getById(long id);

    List<TripRecord> getTrips(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> emails,
            DateSortType sort,
            int page,
            int limit);
}
