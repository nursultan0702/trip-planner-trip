package com.tripplannertrip.repository;

import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.DateSortType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

  @Query("SELECT t FROM TripEntity t " +
      "WHERE (:startDate IS NULL OR t.startDate >= :startDate) " +
      "AND (:endDate IS NULL OR t.endDate <= :endDate) " +
      "AND (:emails IS NULL OR :emails MEMBER OF t.members) " +
      "ORDER BY " +
      "CASE WHEN :sort = 'date_asc' THEN t.startDate END ASC, " +
      "CASE WHEN :sort = 'date_desc' THEN t.startDate END DESC")
  Page<TripEntity> findTrips(@Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("emails") List<String> emails,
                             @Param("sort") DateSortType sort,
                             Pageable pageable);
}
