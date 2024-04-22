package com.tripplannertrip.service.impl;

import static com.tripplannertrip.model.DateSortType.DATE_ASC;
import static com.tripplannertrip.model.NotificationStatus.PENDING;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.NotificationOutbox;
import com.tripplannertrip.entity.PlaceEntity;
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
import com.tripplannertrip.service.TripService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

  private final PlaceRepository placeRepository;
  private final MemberService memberService;
  private final TripRepository tripRepository;
  private final NotificationOutboxRepository notificationOutboxRepository;
  private final TripMapper tripMapper;

  @Override
  public TripRecord createTrip(User user, TripRecord tripRecord) {
    var newTripEntity = buildNewTripEntity(user, tripRecord);
    var savedTrip = tripRepository.save(newTripEntity);
    notifyTripCreation(savedTrip);
    return tripMapper.entityToRecord(savedTrip);
  }

  @Override
  public TripRecord getById(long id) {
    return tripMapper.entityToRecord(getEntityById(id));
  }

  @Override
  public TripEntity getEntityById(long id) {
    return tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
  }

  @Override
  public List<TripRecord> getTripRecordByFilter(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Set<String> emails,
      DateSortType sort,
      int page,
      int limit) {

    var sorting = getSortingType(sort);
    Pageable pageable = PageRequest.of(page, limit, sorting);
    var members = memberService.getMembers(emails);

    Page<TripEntity> tripPage = filterTrips(startDate, endDate, members, pageable);

    return tripPage.getContent().stream()
        .map(tripMapper::entityToRecord)
        .toList();
  }

  @Override
  public List<TripEntity> getTripEntityByFilter(LocalDateTime startDate, LocalDateTime endDate,
                                                Set<String> emails, DateSortType sort, int page,
                                                int limit) {
    Sort sorting = getSortingType(sort);
    Pageable pageable = PageRequest.of(page, limit, sorting);
    Set<MemberEntity> members = memberService.getOrCreateMembers(emails);

    return filterTrips(startDate, endDate, members, pageable).getContent();
  }

  @Override
  public TripRecord updateTrip(long tripId, TripRecord tripRecord) {
    TripEntity tripEntity = getTripEntity(tripId);
    updateTripEntityFromRecord(tripEntity, tripRecord);
    TripEntity updatedTrip = tripRepository.save(tripEntity);
    return tripMapper.entityToRecord(updatedTrip);
  }

  @Override
  public void deleteTrip(long tripId) {
    if (!tripRepository.existsById(tripId)) {
      throw new TripNotFoundException(tripId);
    }
    tripRepository.deleteById(tripId);
  }

  private TripEntity buildNewTripEntity(User user, TripRecord tripRecord) {
    Set<PlaceEntity> places = findExistingPlaces(tripRecord.placeIds());
    Set<MemberEntity> members = memberService.getOrCreateMembers(tripRecord.members());

    return TripEntity.builder()
        .userId(user.getUsername())
        .name(tripRecord.name())
        .description(tripRecord.description())
        .startDate(tripRecord.startDate())
        .endDate(tripRecord.endDate())
        .members(members)
        .places(places)
        .build();
  }

  private void notifyTripCreation(TripEntity tripEntity) {
    NotificationOutbox outbox = NotificationOutbox.builder()
        .status(PENDING)
        .tripEntity(tripEntity)
        .build();
    notificationOutboxRepository.save(outbox);
  }

  private Page<TripEntity> filterTrips(LocalDateTime startDate, LocalDateTime endDate,
                                       Set<MemberEntity> members, Pageable pageable) {

    if (members.isEmpty()) {
      return tripRepository.findByDates(startDate, endDate, pageable);
    } else {
      return tripRepository.findByDatesAndMembers(startDate, endDate, members, pageable);
    }
  }

  private static Sort getSortingType(DateSortType sort) {
    return sort == DATE_ASC ? Sort.by("startDate").ascending() : Sort.by("startDate").descending();
  }

  private TripEntity getTripEntity(long tripId) {
    return tripRepository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException(tripId));
  }

  private void updateTripEntityFromRecord(TripEntity tripEntity, TripRecord tripRecord) {
    tripEntity.setName(tripRecord.name());
    tripEntity.setDescription(tripRecord.description());
    tripEntity.setStartDate(tripRecord.startDate());
    tripEntity.setEndDate(tripRecord.endDate());
    updateMembers(tripEntity, tripRecord.members());
    updatePlaces(tripEntity, tripRecord.placeIds());
  }

  private void updateMembers(TripEntity tripEntity, Set<String> memberEmails) {
    Set<MemberEntity> members = memberService.getOrCreateMembers(memberEmails);
    tripEntity.setMembers(members);
  }

  private void updatePlaces(TripEntity tripEntity, List<Long> placeIds) {
    Set<PlaceEntity> places = findExistingPlaces(placeIds);
    tripEntity.getPlaces().clear();
    tripEntity.getPlaces().addAll(places);
  }

  private Set<PlaceEntity> findExistingPlaces(List<Long> placeIds) {
    if (placeIds == null || placeIds.isEmpty()) {
      return Collections.emptySet();
    }
    return placeIds.stream()
        .map(this::findOrThrowPlace)
        .collect(Collectors.toSet());
  }

  private PlaceEntity findOrThrowPlace(Long id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new PlaceNotFoundException(id));
  }
}
