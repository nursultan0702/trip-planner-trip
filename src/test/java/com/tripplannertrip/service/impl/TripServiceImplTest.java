package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.NotificationOutbox;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.exception.TripNotFoundException;
import com.tripplannertrip.mapper.TripMapper;
import com.tripplannertrip.model.DateSortType;
import com.tripplannertrip.model.TripRecord;
import com.tripplannertrip.repository.NotificationOutboxRepository;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.repository.TripRepository;
import com.tripplannertrip.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceImplTest {

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
  public void createTrip_ValidInputs_ReturnsTripRecord() {
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

    // When
    TripRecord result = tripService.createTrip(user, tripRecord);

    // Then
    assertNotNull(result);
    verify(notificationOutboxRepository).save(any(NotificationOutbox.class));
  }

  @Test
  public void getById_TripExists_ReturnsTripRecord() {
    // Given
    long id = 1L;
    TripEntity tripEntity = new TripEntity();
    TripRecord tripRecord = TripRecord.builder().build();
    when(tripRepository.findById(id)).thenReturn(Optional.of(tripEntity));
    when(tripMapper.entityToRecord(tripEntity)).thenReturn(tripRecord);

    // When
    TripRecord result = tripService.getById(id);

    // Then
    assertEquals(tripRecord, result);
  }

  @Test
  public void getById_TripNotFound_ThrowsException() {
    // Given
    long id = 1L;
    when(tripRepository.findById(id)).thenReturn(Optional.empty());

    // Then
    assertThrows(TripNotFoundException.class, () -> tripService.getById(id));
  }

  @Test
  public void updateTrip_TripExists_UpdatesAndReturnsRecord() {
    // Given
    long id = 1L;
    TripRecord updatedRecord = TripRecord.builder().build();
    TripEntity existingTrip = new TripEntity();
    when(tripRepository.findById(id)).thenReturn(Optional.of(existingTrip));
    when(tripRepository.save(existingTrip)).thenReturn(existingTrip);
    when(tripMapper.entityToRecord(existingTrip)).thenReturn(updatedRecord);

    // When
    TripRecord result = tripService.updateTrip(id, updatedRecord);

    // Then
    assertNotNull(result);
  }

  @Test
  public void deleteTrip_TripExists_DeletesTrip() {
    // Given
    long id = 1L;
    doNothing().when(tripRepository).deleteById(id);

    // When
    assertDoesNotThrow(() -> tripService.deleteTrip(id));

    // Then
    verify(tripRepository).deleteById(id);
  }


  @Test
  public void getTripRecordByFilter_ValidInputs_ReturnsFilteredRecords() {
    // Given
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    Set<String> emails = Set.of("user@example.com");
    DateSortType sortType = DateSortType.DATE_ASC;
    int page = 0;
    int limit = 10;
    Pageable pageable = PageRequest.of(page, limit, Sort.by("startDate").ascending());

    Set<MemberEntity> members = Set.of(new MemberEntity());
    when(memberService.getOrCreateMembers(emails)).thenReturn(members);

    List<TripEntity> tripEntities = List.of(new TripEntity());
    Page<TripEntity> tripPage = new PageImpl<>(tripEntities);
    when(tripRepository.findByStartDateAfterAndEndDateBeforeAndAndMembersIsIn(any(), any(), eq(members), eq(pageable)))
        .thenReturn(tripPage);

    TripRecord tripRecord = TripRecord.builder().build();
    when(tripMapper.entityToRecord(any(TripEntity.class))).thenReturn(tripRecord);

    // When
    List<TripRecord> results = tripService.getTripRecordByFilter(startDate, endDate, emails, sortType, page, limit);

    // Then
    assertFalse(results.isEmpty());
    assertEquals(1, results.size());
    assertEquals(tripRecord, results.get(0));
  }

  @Test
  public void getTripRecordByFilter_NoMembersFound_ReturnsEmptyList() {
    // Given
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = LocalDateTime.now().plusDays(10);
    Set<String> emails = Set.of("user@example.com");
    DateSortType sortType = DateSortType.DATE_ASC;
    int page = 0;
    int limit = 10;
    Pageable pageable = PageRequest.of(page, limit, Sort.by("startDate").ascending());

    when(memberService.getOrCreateMembers(emails)).thenReturn(new HashSet<>()); // No members found

    when(tripRepository.findByStartDateAfterAndEndDateBefore(any(), any(), eq(pageable)))
        .thenReturn(Page.empty());

    // When
    List<TripRecord> results = tripService.getTripRecordByFilter(startDate, endDate, emails, sortType, page, limit);

    // Then
    assertTrue(results.isEmpty());
  }

  @Test
  public void updateTrip_NotFound_ThrowsException() {
    // Given
    long tripId = 1L;
    TripRecord updatedRecord = TripRecord.builder().build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

    // Then
    assertThrows(TripNotFoundException.class, () -> tripService.updateTrip(tripId, updatedRecord));
  }
}
