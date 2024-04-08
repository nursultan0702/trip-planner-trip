package com.tripplannertrip.repository;

import com.tripplannertrip.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

    List<Long> findExistingPlaceEntityIds(List<Long> ids);

}
