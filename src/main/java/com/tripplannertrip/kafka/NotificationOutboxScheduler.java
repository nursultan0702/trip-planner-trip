package com.tripplannertrip.kafka;

import com.tripplannertrip.entity.NotificationOutbox;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.mapper.PlaceMapper;
import com.tripplannertrip.mapper.TripMapper;
import com.tripplannertrip.model.NotificationRecord;
import com.tripplannertrip.model.NotificationStatus;
import com.tripplannertrip.model.PlaceRecord;
import com.tripplannertrip.repository.NotificationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tripplannertrip.model.NotificationStatus.PENDING;
import static com.tripplannertrip.model.NotificationStatus.PUBLISHED;

@Component
@RequiredArgsConstructor
public class NotificationOutboxScheduler {

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final MessageProducer messageProducer;
    private final TripMapper tripMapper;
    private final PlaceMapper placeMapper;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void schedulePendingNotificationOutbox() {
        notificationOutboxRepository.findByStatuses(List.of(PENDING.name()))
                .forEach(this::sendNotificationOutbox);

    }

    private void sendNotificationOutbox(NotificationOutbox notificationOutbox) {
        TripEntity tripEntity = notificationOutbox.getTripEntity();
        Set<PlaceRecord> places = tripEntity.getPlaces().stream()
                .map(placeMapper::placeEntityToPlaceRecord)
                .collect(Collectors.toSet());

        var notification = NotificationRecord.builder()
                .trip(tripMapper.entityToRecord(tripEntity))
                .places(places).build();

        messageProducer.sendMessage(notification);

        notificationOutbox.setStatus(PUBLISHED);
        notificationOutboxRepository.save(notificationOutbox);
    }

//    @Scheduled(fixedDelay = 3000)
//    @Transactional
//    public void schedulePublishedNotificationOutbox() {
//        var byStatusesList = notificationOutboxRepository.findByStatuses(List.of(PUBLISHED.name()));
//        notificationOutboxRepository.deleteAll(byStatusesList);
//
//    }
}
