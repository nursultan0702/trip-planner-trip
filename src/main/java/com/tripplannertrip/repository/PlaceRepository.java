package com.tripplannertrip.repository;

import com.tripplannertrip.entity.PlaceEntity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

  @Query("SELECT p FROM PlaceEntity p JOIN p.trips t WHERE t.id = ?1")
  List<PlaceEntity> findAllByTripId(Long tripId);

  @Query("SELECT DISTINCT p FROM PlaceEntity p " +
          "JOIN p.trips t " +
          "JOIN t.members m " +
          "WHERE p.country = :country " +
          "AND (:startDate IS NULL OR t.startDate >= :startDate) " +
          "AND (:endDate IS NULL OR t.endDate <= :endDate) " +
          "AND (:emails IS NULL OR m.email IN :emails)")
  List<PlaceEntity> findPlacesByCriteria(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("emails") List<String> emails,
                                         @Param("country") String country);

}
