package com.tripplannertrip.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tripName;
    private String tripDescription;
    private LocalDateTime tripStartDate;
    private LocalDateTime tripEndDate;
    private String tripStatus;
    private String country;
    @ElementCollection
    private List<Long> members;
}
