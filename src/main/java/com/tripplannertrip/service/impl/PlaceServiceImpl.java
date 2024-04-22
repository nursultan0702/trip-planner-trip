package com.tripplannertrip.service.impl;

import static com.tripplannertrip.model.DateSortType.DATE_ASC;

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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    var trip = tripRepository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException(tripId));
    return placeRepository.findByTripsIsIn(Set.of(trip))
        .stream()
        .map(placeMapper::placeEntityToPlaceRecord)
        .toList();
  }


  @Override
  public List<PlaceRecord> getPlacesByCriteria(LocalDateTime startDate, LocalDateTime endDate,
                                               Set<String> emails, String country, int page,
                                               int limit) {
    var trips =
        tripService.getTripEntityByFilter(startDate, endDate, emails, DATE_ASC, page, limit);
    return trips.stream()
        .map(TripEntity::getPlaces)
        .flatMap(Collection::stream)
        .map(placeMapper::placeEntityToPlaceRecord)
        .distinct()
        .toList();
  }


  @Override
  public PlaceRecord getPlaceById(Long id) {
    var place = placeRepository.findById(id)
        .orElseThrow(() -> new PlaceNotFoundException(id));
    return placeMapper.placeEntityToPlaceRecord(place);
  }


  @Override
  public PlaceRecord createPlace(PlaceRecord placeRecord) {
    var placeEntity = placeMapper.placeRecordToPlaceEntity(placeRecord);
    setTrip(placeRecord, placeEntity);
    return placeMapper.placeEntityToPlaceRecord(placeRepository.save(placeEntity));
  }


  private void setTrip(PlaceRecord placeRecord, PlaceEntity placeEntity) {
    Optional.ofNullable(placeRecord.tripIds())
        .ifPresent(tripIds -> placeEntity.getTrips().addAll(getTrips(tripIds)));
  }

  private Set<TripEntity> getTrips(Set<Long> tripIds) {
    return tripIds.stream()
        .map(tripId -> tripRepository.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId)))
        .collect(Collectors.toSet());
  }


  @Override
  public PlaceRecord updatePlace(Long id, PlaceRecord placeRecord) {
    var placeEntity = placeRepository.findById(id)
        .orElseThrow(() -> new PlaceNotFoundException(id));
    updatePlaceEntityFromPlaceRecord(placeRecord, placeEntity);
    return placeMapper.placeEntityToPlaceRecord(placeRepository.save(placeEntity));
  }

  @Override
  public void deletePlace(Long id) {
    if (!placeRepository.existsById(id)) {
      throw new PlaceNotFoundException(id);
    }
    placeRepository.deleteById(id);
  }

  public void updatePlaceEntityFromPlaceRecord(PlaceRecord placeRecord, PlaceEntity placeEntity) {
    // Update basic fields
    placeEntity.setCountry(
        Optional.ofNullable(placeRecord.country()).orElse(placeEntity.getCountry()));
    placeEntity.setCity(Optional.ofNullable(placeRecord.city()).orElse(placeEntity.getCity()));
    placeEntity.setName(Optional.ofNullable(placeRecord.name()).orElse(placeEntity.getName()));

    // Update images, replace the list contents
    placeEntity.getImages().clear();
    if (placeRecord.images() != null) {
      placeEntity.getImages().addAll(placeRecord.images());
    }

    // Update trips relationship
    if (placeRecord.tripIds() != null && !placeRecord.tripIds().isEmpty()) {
      updateTrips(placeRecord.tripIds(), placeEntity);
    }
  }

  private void updateTrips(Set<Long> newTripIds, PlaceEntity placeEntity) {
    Set<TripEntity> currentTrips = placeEntity.getTrips();
    List<TripEntity> newTrips = tripRepository.findAllById(newTripIds);

    // Remove the trips that are no longer associated
    currentTrips.removeIf(trip -> !newTripIds.contains(trip.getId()));

    // Add new trips that are not currently associated
    newTrips.forEach(trip -> {
      if (currentTrips.stream().noneMatch(t -> t.getId().equals(trip.getId()))) {
        currentTrips.add(trip);
      }
    });
  }
}
