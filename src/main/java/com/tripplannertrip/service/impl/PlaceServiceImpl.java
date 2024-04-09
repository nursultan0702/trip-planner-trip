package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.mapper.PlaceMapper;
import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.service.PlaceService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final TripRepository tripRepository;
    private final PlaceMapper placeMapper;

    @Override
    public Set<PlaceEntity> findExistingPlaces(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        return ids.stream()
                .map(id -> placeRepository.findById(id)
                        .orElseThrow(() -> new PlaceNotFoundException(id)))
                .collect(Collectors.toSet());
    }

    @Override
    public List<PlaceRecord> getAllPlacesByTripId(Long tripId) {
        List<PlaceEntity> allByTripId = placeRepository.findAllByTripId(tripId);
        return allByTripId.stream()
                .map(placeMapper::placeEntityToPlaceRecord)
                .toList();
    }

    @Override
    public List<PlaceEntity> getPlacesByCriteria(LocalDateTime startDate,
                                                 LocalDateTime endDate,
                                                 List<String> emails,
                                                 String country) {
        return placeRepository.findPlacesByCriteria(startDate, endDate, emails, country);
    }

    @Override
    public PlaceRecord getPlaceById(Long id) {
        var place = placeRepository.findById(id).orElseThrow(() -> new PlaceNotFoundException(id));
        return placeMapper.placeEntityToPlaceRecord(place);
    }

    @Override
    public PlaceRecord createPlace(PlaceRecord placeRecord) {
        var placeEntity = placeMapper.placeRecordToPlaceEntity(placeRecord);
        Long tripId = placeRecord.tripId();

        if (tripId != null) {
            var trip =
                    tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
            placeEntity.getTrips().add(trip);
        }

        placeEntity = placeRepository.save(placeEntity);
        return placeMapper.placeEntityToPlaceRecord(placeEntity);
    }

    @Override
    public PlaceRecord updatePlace(Long id, PlaceRecord placeRecord) {
        var placeEntity =
                placeRepository.findById(id)
                        .orElseThrow(() -> new PlaceNotFoundException(id));

        placeEntity.setCountry(placeRecord.country());
        placeEntity.setCity(placeRecord.city());
        placeEntity.setName(placeRecord.name());
        placeEntity.setImages(placeRecord.images());

        placeEntity = placeRepository.save(placeEntity);
        return placeMapper.placeEntityToPlaceRecord(placeEntity);
    }

    @Override
    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }

}
