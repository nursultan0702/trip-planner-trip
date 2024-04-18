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
import java.util.HashSet;
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

    var places = findExistingPlaces(tripRecord.placeIds());
    var members = memberService.getOrCreateMembers(tripRecord.members());

    var newTripEntity = TripEntity.builder()
        .userId(user.getUsername())
        .name(tripRecord.name())
        .description(tripRecord.description())
        .startDate(tripRecord.startDate())
        .endDate(tripRecord.endDate())
        .members(members)
        .places(places)
        .build();

    var savedTrip = tripRepository.save(newTripEntity);
    createOutboxMessage(savedTrip);

    return tripMapper.entityToRecord(savedTrip);
  }

  private void createOutboxMessage(TripEntity savedTrip) {
    var outbox = NotificationOutbox.builder()
        .status(PENDING)
        .tripEntity(savedTrip)
        .build();
    notificationOutboxRepository.save(outbox);
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
    var members = memberService.getOrCreateMembers(emails);

    Page<TripEntity> tripPage = getTripByFilter(startDate, endDate, members, pageable);

    return tripPage.getContent().stream()
        .map(tripMapper::entityToRecord)
        .toList();
  }

  @Override
  public List<TripEntity> getTripEntityByFilter(LocalDateTime startDate, LocalDateTime endDate,
                                                Set<String> emails, DateSortType sort, int page,
                                                int limit) {
    var sorting = getSortingType(sort);
    Pageable pageable = PageRequest.of(page, limit, sorting);
    var members = memberService.getOrCreateMembers(emails);

    return getTripByFilter(startDate, endDate, members, pageable).getContent();
  }

  private Page<TripEntity> getTripByFilter(LocalDateTime startDate, LocalDateTime endDate,
                                           Set<MemberEntity> members, Pageable pageable) {
    boolean membersPresent = members != null && !members.isEmpty();

    if (startDate != null && endDate != null) {
      return membersPresent ?
          tripRepository.findByStartDateAfterAndEndDateBeforeAndAndMembersIsIn(startDate, endDate,
              members, pageable) :
          tripRepository.findByStartDateAfterAndEndDateBefore(startDate, endDate, pageable);
    } else if (startDate != null) {
      return membersPresent ?
          tripRepository.findByStartDateAfterAndMembersIsIn(startDate, members, pageable) :
          tripRepository.findByStartDateAfter(startDate, pageable);
    } else if (endDate != null) {
      return membersPresent ?
          tripRepository.findByEndDateBeforeAndMembersIsIn(endDate, members, pageable) :
          tripRepository.findByEndDateBefore(endDate, pageable);
    } else {
      return Page.empty();
    }
  }

  private static Sort getSortingType(DateSortType sort) {
    return sort == DATE_ASC ? Sort.by("startDate").ascending() : Sort.by("startDate").descending();
  }


  @Override
  public TripRecord updateTrip(long tripId, TripRecord tripRecord) {
    var tripEntity = tripRepository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException(tripId));

    var places = findExistingPlaces(tripRecord.placeIds());
    var members = memberService.getOrCreateMembers(tripRecord.members());

    tripEntity.setName(tripRecord.name());
    tripEntity.setDescription(tripRecord.description());
    tripEntity.setStartDate(tripRecord.startDate());
    tripEntity.setEndDate(tripRecord.endDate());
    tripEntity.setMembers(members);
    tripEntity.getPlaces().addAll(places);

    tripEntity = tripRepository.save(tripEntity);

    return tripMapper.entityToRecord(tripEntity);
  }

  @Override
  public void deleteTrip(long tripId) {
    tripRepository.deleteById(tripId);
  }

  private Set<PlaceEntity> findExistingPlaces(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return new HashSet<>();
    }

    return ids.stream()
        .map(id -> placeRepository.findById(id)
            .orElseThrow(() -> new PlaceNotFoundException(id)))
        .collect(Collectors.toSet());
  }
}
