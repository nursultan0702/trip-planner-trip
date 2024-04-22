package com.tripplannertrip.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.NotificationOutbox;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.mapper.TripMapper;
import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.repository.NotificationOutboxRepository;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.service.MemberService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

  @Mock
  private PlaceRepository placeRepository;
  @Mock
  private MemberService memberService;
  @Mock
  private TripRepository tripRepository;
  @Mock
  private NotificationOutboxRepository notificationOutboxRepository;
  @Mock
  private TripMapper tripMapper;

  @InjectMocks
  private TripServiceImpl tripService;

  @Test
  void createTrip_ValidInputs_ReturnsTripRecord() {
    // Given
    User user = new User("username", "password", Collections.emptyList());
    TripRecord tripRecord = TripRecord.builder()
        .name("Trip to Paris")
        .description("A beautiful trip")
        .startDate(LocalDateTime.now())
        .endDate(LocalDateTime.now().plusDays(5))
        .members(Collections.emptySet())
        .placeIds(Collections.emptyList()).build();

    TripEntity savedTrip = new TripEntity();
    when(memberService.getOrCreateMembers(any())).thenReturn(new HashSet<>());
    when(tripRepository.save(any(TripEntity.class))).thenReturn(savedTrip);
    when(tripMapper.entityToRecord(savedTrip)).thenReturn(tripRecord);

    TripRecord result = tripService.createTrip(user, tripRecord);

    assertNotNull(result);
    verify(notificationOutboxRepository).save(any(NotificationOutbox.class));
  }

  @Test
  void getById_TripExists_ReturnsTripRecord() {
    long id = 1L;
    TripEntity tripEntity = new TripEntity();
    TripRecord tripRecord = TripRecord.builder().build();
    when(tripRepository.findById(id)).thenReturn(Optional.of(tripEntity));
    when(tripMapper.entityToRecord(tripEntity)).thenReturn(tripRecord);

    TripRecord result = tripService.getById(id);

    assertEquals(tripRecord, result);
  }

  @Test
  void getById_TripNotFound_ThrowsException() {
    long id = 1L;
    when(tripRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(TripNotFoundException.class, () -> tripService.getById(id));
  }

  @Test
  void updateTrip_TripExists_UpdatesAndReturnsRecord() {
    long id = 1L;
    TripRecord updatedRecord = TripRecord.builder().build();
    TripEntity existingTrip = new TripEntity();
    when(tripRepository.findById(id)).thenReturn(Optional.of(existingTrip));
    when(tripRepository.save(existingTrip)).thenReturn(existingTrip);
    when(tripMapper.entityToRecord(existingTrip)).thenReturn(updatedRecord);

    TripRecord result = tripService.updateTrip(id, updatedRecord);

    assertNotNull(result);
  }

  @Test
  void deleteTrip_TripExists_DeletesSuccessfully() {
    long tripId = 1L;
    when(tripRepository.existsById(tripId)).thenReturn(true);

    tripService.deleteTrip(tripId);

    verify(tripRepository).deleteById(tripId);
  }

  @Test
  void deleteTrip_TripDoesNotExist_ThrowsTripNotFoundException() {
    long tripId = 2L;
    when(tripRepository.existsById(tripId)).thenReturn(false);

    Exception exception =
        assertThrows(TripNotFoundException.class, () -> tripService.deleteTrip(tripId));

    assertEquals("Trip not found with id " + tripId, exception.getMessage());
  }


  @Test
  void getTripRecordByFilter_ValidInputs_ReturnsFilteredRecords() {
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    Set<String> emails = Set.of("user@example.com");
    DateSortType sortType = DateSortType.DATE_ASC;
    int page = 0;
    int limit = 10;
    Pageable pageable = PageRequest.of(page, limit, Sort.by("startDate").ascending());

    List<TripEntity> tripEntities = List.of(new TripEntity());
    Page<TripEntity> tripPage = new PageImpl<>(tripEntities);
    when(tripRepository.findByDates(any(), any(), eq(pageable)))
        .thenReturn(tripPage);

    TripRecord tripRecord = TripRecord.builder().build();
    when(tripMapper.entityToRecord(any(TripEntity.class))).thenReturn(tripRecord);

    List<TripRecord> results =
        tripService.getTripRecordByFilter(startDate, endDate, emails, sortType, page, limit);

    assertFalse(results.isEmpty());
    assertEquals(1, results.size());
    assertEquals(tripRecord, results.get(0));
  }

  @Test
  void getTripRecordByFilter_NoMembersFound_ReturnsEmptyList() {
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = startDate.plusDays(10);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").ascending());

    Page<TripEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(
        tripRepository.findByDates(eq(startDate), eq(endDate), eq(pageable)))
        .thenReturn(emptyPage);

    List<TripRecord> results =
        tripService.getTripRecordByFilter(startDate, endDate, new HashSet<>(),
            DateSortType.DATE_ASC, 0, 10);

    assertTrue(results.isEmpty());
  }

  @Test
  void updateTrip_NotFound_ThrowsException() {
    long tripId = 1L;
    TripRecord updatedRecord = TripRecord.builder().build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    assertThrows(TripNotFoundException.class, () -> tripService.updateTrip(tripId, updatedRecord));
  }
}
