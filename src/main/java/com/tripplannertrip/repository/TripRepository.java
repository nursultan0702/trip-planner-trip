package com.tripplannertrip.repository;

import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.DateSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

    @Query("SELECT t FROM TripEntity t " +
            "WHERE (?1 IS NULL OR t.startDate >= ?1) " +
            "AND (?2 IS NULL OR t.endDate <= ?2) " +
            "AND (?3 IS NULL OR t.members IN ?3) " +
            "ORDER BY " +
            "CASE WHEN ?4 = 'date_asc' THEN t.startDate END ASC, " +
            "CASE WHEN ?4 = 'date_desc' THEN t.startDate END DESC")
    Page<TripEntity> findTrips(LocalDateTime startDate,
                               LocalDateTime endDate,
                               List<String> emails,
                               DateSortType sort,
                               Pageable pageable);
}
