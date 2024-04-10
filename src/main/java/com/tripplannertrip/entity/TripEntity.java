package com.tripplannertrip.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private String userId;
  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  @ManyToMany
  @JoinTable(
      name = "trip_members", // Custom join table name
      joinColumns = @JoinColumn(name = "trip_id"),
      inverseJoinColumns = @JoinColumn(name = "member_email") // Referencing the ID (email) of Member
  )
  private Set<MemberEntity> members = new HashSet<>();
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(
      name = "trip_place",
      joinColumns = @JoinColumn(name = "trip_id"),
      inverseJoinColumns = @JoinColumn(name = "place_id"))
  private Set<PlaceEntity> places = new HashSet<>();
}
