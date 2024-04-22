package com.tripplannertrip.repository;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.TripEntity;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {

  @Query("SELECT t FROM TripEntity t JOIN t.members m WHERE t.startDate BETWEEN :startDate AND :endDate AND m IN :members")
  Page<TripEntity> findByDatesAndMembers(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Set<MemberEntity> members,
      Pageable pageable);


  @Query("SELECT t FROM TripEntity t WHERE t.startDate BETWEEN :startDate AND :endDate")
  Page<TripEntity> findByDates(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Pageable pageable);
}
