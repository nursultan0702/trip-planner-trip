package com.tripplannertrip.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String city;
    private String name;
    @ElementCollection
    private List<String> images;
    @ManyToMany(mappedBy = "places", fetch = FetchType.LAZY)
    private Set<TripEntity> trips = new HashSet<>();
}
