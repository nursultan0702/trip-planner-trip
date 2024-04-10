package com.tripplannertrip.repository;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.entity.TripEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

  List<PlaceEntity> findByTripsIsIn(Set<TripEntity> trips);
}
