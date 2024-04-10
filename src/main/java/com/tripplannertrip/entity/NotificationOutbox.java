package com.tripplannertrip.entity;

import com.tripplannertrip.model.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notificationId;

    @OneToOne(fetch = FetchType.EAGER)
    private TripEntity tripEntity;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

}
