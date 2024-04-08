package com.tripplannertrip.service;

import com.tripplannertrip.entity.PlaceEntity;

import java.util.List;
import java.util.Set;

public interface PlaceService {
    Set<PlaceEntity> findExistingPlaces(List<Long> ids);
}
