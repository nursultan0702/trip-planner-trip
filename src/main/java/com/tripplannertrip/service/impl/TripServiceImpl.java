package com.tripplannertrip.service.impl;

import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.mapper.TripMapper;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.service.PlaceService;
import com.tripplannertrip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final PlaceService placeService;
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public TripRecord createTrip(TripRecord tripRecord) {

        var places = placeService.findExistingPlaces(tripRecord.placeIds());
        var newTripEntity = TripEntity.builder()
                .name(tripRecord.name())
                .description(tripRecord.description())
                .startDate(tripRecord.startDate())
                .endDate(tripRecord.endDate())
                .places(places)
                .build();
        var savedTrip = tripRepository.save(newTripEntity);

        return tripMapper.tripEntityToRecord(savedTrip);
    }

    @Override
    public TripRecord getById(long id) {
        var trip = tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
        return tripMapper.tripEntityToRecord(trip);
    }

    @Override
    public List<TripRecord> getTrips(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> emails,
            DateSortType sort,
            int page,
            int limit) {

        Pageable pageable = PageRequest.of(page, limit);

        Page<TripEntity> tripPage = tripRepository.findTrips(startDate, endDate, emails, sort, pageable);

        return tripPage.getContent().stream()
                .map(tripMapper::tripEntityToRecord)
                .collect(Collectors.toList());
    }
}
