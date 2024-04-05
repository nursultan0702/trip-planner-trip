package com.tripplannertrip.service.impl;

import com.tripplannertrip.TripRepository;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    public TripRecord createTrip(TripRecord tripRecord) {
        TripEntity newTrip = TripEntity.builder()
                .tripName(tripRecord.tripName())
                .build();
        return null;
    }
}
