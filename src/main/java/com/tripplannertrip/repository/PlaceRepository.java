package com.tripplannertrip.repository;

import com.tripplannertrip.entity.PlaceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

  @Query("SELECT p FROM PlaceEntity p JOIN p.trips t WHERE t.id = ?1")
  List<PlaceEntity> findAllByTripId(Long tripId);

}
