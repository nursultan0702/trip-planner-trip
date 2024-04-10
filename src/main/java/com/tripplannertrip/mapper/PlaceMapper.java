package com.tripplannertrip.mapper;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.PlaceRecord;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlaceMapper {

  PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);


  @Mapping(source = "id", target = "placeId")
  @Mapping(source = "trips", target = "tripIds", qualifiedByName = "tripIds")
  PlaceRecord placeEntityToPlaceRecord(PlaceEntity placeEntity);

  @Mapping(target = "id", source = "placeId")
  PlaceEntity placeRecordToPlaceEntity(PlaceRecord placeRecord);

  @Named("tripIds")
  default Set<Long> mapTripEntitiesToIds(Set<TripEntity> trips) {
    if (trips == null || trips.isEmpty()) {
      return Collections.emptySet();
    }
    return trips.stream()
        .map(TripEntity::getId)
        .collect(Collectors.toSet());
  }

}
