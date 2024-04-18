package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.mapper.PlaceMapper;
import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.service.PlaceService;
import com.tripplannertrip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tripplannertrip.model.DateSortType.DATE_ASC;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

  private final PlaceRepository placeRepository;
  private final TripRepository tripRepository;
  private final TripService tripService;
  private final PlaceMapper placeMapper;

  @Override
  public List<PlaceRecord> getAllPlacesByTripId(Long tripId) {
    var trip =
        tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
    var allByTripId = placeRepository.findByTripsIsIn(Set.of(trip));

    return allByTripId.stream()
        .map(placeMapper::placeEntityToPlaceRecord)
        .toList();
  }

  @Override
  public List<PlaceRecord> getPlacesByCriteria(LocalDateTime startDate, LocalDateTime endDate,
                                               Set<String> emails,
                                               String country, int page, int limit) {
    var trips =
        tripService.getTripEntityByFilter(startDate, endDate, emails, DATE_ASC, page, limit);

    return trips.stream().map(TripEntity::getPlaces).flatMap(Collection::stream)
        .map(placeMapper::placeEntityToPlaceRecord).toList();
  }

  @Override
  public PlaceRecord getPlaceById(Long id) {
    var place = placeRepository
        .findById(id)
        .orElseThrow(() -> new PlaceNotFoundException(id));

    return placeMapper.placeEntityToPlaceRecord(place);
  }

  @Override
  public PlaceRecord createPlace(PlaceRecord placeRecord) {

    var placeEntity = placeMapper.placeRecordToPlaceEntity(placeRecord);
    setTrip(placeRecord, placeEntity);

    placeEntity = placeRepository.save(placeEntity);
    return placeMapper.placeEntityToPlaceRecord(placeEntity);
  }

  private void setTrip(PlaceRecord placeRecord, PlaceEntity placeEntity) {
    var tripIds = placeRecord.tripIds();
    if (tripIds != null) {
      var trips = getTrips(tripIds);
      placeEntity.getTrips().addAll(trips);
    }
  }

  private Set<TripEntity> getTrips(Set<Long> tripIds) {
    return tripIds.stream()
        .map(tripId -> tripRepository.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId))).collect(Collectors.toSet());
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
