package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.mapper.PlaceMapper;
import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.service.TripService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.tripplannertrip.model.DateSortType.DATE_ASC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceImplTest {

  @Mock
  private PlaceRepository placeRepository;
  @Mock
  private TripRepository tripRepository;
  @Mock
  private TripService tripService;
  @Mock
  private PlaceMapper placeMapper;

  @InjectMocks
  private PlaceServiceImpl placeService;

  @Test
  public void testGetAllPlacesByTripId_Found() {
    Long tripId = 1L;
    TripEntity trip = new TripEntity();
    PlaceEntity place = new PlaceEntity();
    PlaceRecord placeRecord = PlaceRecord.builder().build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    when(placeRepository.findByTripsIsIn(Set.of(trip))).thenReturn(List.of(place));
    when(placeMapper.placeEntityToPlaceRecord(place)).thenReturn(placeRecord);

    List<PlaceRecord> results = placeService.getAllPlacesByTripId(tripId);
    assertEquals(1, results.size());
    assertTrue(results.contains(placeRecord));
  }

  @Test
  public void testGetAllPlacesByTripId_TripNotFound() {
    Long tripId = 1L;
    when(tripRepository.findById(tripId)).thenThrow(new TripNotFoundException(tripId));
    assertThrows(TripNotFoundException.class, () -> placeService.getAllPlacesByTripId(tripId));
  }

  @Test
  public void testGetPlaceById_Found() {
    Long id = 1L;
    PlaceEntity place = new PlaceEntity();
    PlaceRecord expected = PlaceRecord.builder().build();
    when(placeRepository.findById(id)).thenReturn(Optional.of(place));
    when(placeMapper.placeEntityToPlaceRecord(place)).thenReturn(expected);

    PlaceRecord result = placeService.getPlaceById(id);
    assertEquals(expected, result);
  }

  @Test
  public void testGetPlaceById_NotFound() {
    Long id = 1L;
    when(placeRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(PlaceNotFoundException.class, () -> placeService.getPlaceById(id));
  }

  @Test
  public void testCreatePlace() {
    PlaceRecord inputRecord =
        PlaceRecord.builder()
            .name("Spain")
            .city("Madrid")
            .name("Plaza Mayor")
            .images(Collections.emptyList()).build();
    PlaceEntity mappedEntity = new PlaceEntity();
    when(placeMapper.placeRecordToPlaceEntity(inputRecord)).thenReturn(mappedEntity);
    when(placeRepository.save(any(PlaceEntity.class))).thenReturn(mappedEntity);
    when(placeMapper.placeEntityToPlaceRecord(mappedEntity)).thenReturn(inputRecord);

    PlaceRecord result = placeService.createPlace(inputRecord);
    assertEquals(inputRecord, result);
    verify(placeRepository).save(mappedEntity);
  }

  @Test
  public void testUpdatePlace_FoundAndUpdated() {
    Long id = 1L;
    PlaceRecord updateRecord = PlaceRecord.builder()
        .placeId(id)
        .name("Name")
        .country("France")
        .city("Paris")
        .images(Collections.emptyList()).build();
    PlaceEntity existingEntity = new PlaceEntity();
    when(placeRepository.findById(id)).thenReturn(Optional.of(existingEntity));
    when(placeRepository.save(existingEntity)).thenReturn(existingEntity);
    when(placeMapper.placeEntityToPlaceRecord(existingEntity)).thenReturn(updateRecord);

    PlaceRecord result = placeService.updatePlace(id, updateRecord);
    assertEquals(updateRecord, result);
  }

  @Test
  public void testUpdatePlace_NotFound() {
    Long id = 1L;
    PlaceRecord updateRecord = PlaceRecord.builder().build();
    when(placeRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(PlaceNotFoundException.class, () -> placeService.updatePlace(id, updateRecord));
  }

  @Test
  public void testDeletePlace() {
    Long id = 1L;
    doNothing().when(placeRepository).deleteById(id);

    assertDoesNotThrow(() -> placeService.deletePlace(id));
    verify(placeRepository).deleteById(id);
  }

  @Test
  public void testGetPlacesByCriteria() {
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    Set<String> emails = Set.of("example@example.com");
    String country = "USA";
    int page = 0;
    int limit = 10;

    TripEntity trip = mock(TripEntity.class);
    List<TripEntity> trips = List.of(trip);
    PlaceEntity placeEntity = new PlaceEntity();
    PlaceRecord placeRecord = PlaceRecord.builder().build();

    when(tripService.getTripEntityByFilter(startDate, endDate, emails, DATE_ASC, page, limit)).thenReturn(trips);
    when(trip.getPlaces()).thenReturn(Set.of(placeEntity));
    when(placeMapper.placeEntityToPlaceRecord(placeEntity)).thenReturn(placeRecord);

    List<PlaceRecord> results = placeService.getPlacesByCriteria(startDate, endDate, emails, country, page, limit);
    assertTrue(results.contains(placeRecord));
    assertEquals(1, results.size());
  }

}
