package com.tripplannertrip.repository;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.TripEntity;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, Long> {
  Page<TripEntity> findByStartDateAfterAndEndDateBeforeAndAndMembersIsIn(
      LocalDateTime startDate, LocalDateTime endDate, Set<MemberEntity> members, Pageable pageable);

  Page<TripEntity> findByStartDateAfterAndEndDateBefore(LocalDateTime startDate,
                                                        LocalDateTime endDate,
                                                        Pageable pageable);

  Page<TripEntity> findByStartDateAfter(LocalDateTime startDate,
                                        Pageable pageable);

  Page<TripEntity> findByEndDateBefore(LocalDateTime endDate,
                                       Pageable pageable);


  Page<TripEntity> findByStartDateAfterAndMembersIsIn(LocalDateTime startDate,
                                                      Set<MemberEntity> members,
                                                      Pageable pageable);

  Page<TripEntity> findByEndDateBeforeAndMembersIsIn(LocalDateTime endDate,
                                                     Set<MemberEntity> members,
                                                     Pageable pageable);


}
