package com.tripplannertrip.mapper;

import com.tripplannertrip.entity.MemberEntity;
import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.TripRecord;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripMapper {

  TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

  @Mapping(target = "members", source = "members", qualifiedByName = "memberEntitiesToStrings")
  @Mapping(target = "placeIds", source = "places")
  @Mapping(source = "id", target = "tripId")
  TripRecord entityToRecord(TripEntity entity);

  default List<Long> mapPlaces(Set<PlaceEntity> places) {
    if (places == null) {
      return new ArrayList<>();
    }
    return places.stream()
        .map(PlaceEntity::getId) // Assuming getId() exists in PlaceEntity
        .toList();
  }

  @Named("memberEntitiesToStrings")
  default Set<String> memberEntitiesToStrings(Set<MemberEntity> memberEntities) {
    if (memberEntities == null) {
      return new HashSet<>();
    }
    return memberEntities.stream()
        .map(MemberEntity::getEmail)
        .collect(Collectors.toSet());
  }
}

