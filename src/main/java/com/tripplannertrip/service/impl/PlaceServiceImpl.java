package com.tripplannertrip.service.impl;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.exception.PlaceNotFoundException;
import com.tripplannertrip.repository.PlaceRepository;
import com.tripplannertrip.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public Set<PlaceEntity> findExistingPlaces(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        return ids.stream()
                .map(id -> placeRepository.findById(id)
                        .orElseThrow(() -> new PlaceNotFoundException("Place with id " + id + " not found")))
                .collect(Collectors.toSet());
    }
}
