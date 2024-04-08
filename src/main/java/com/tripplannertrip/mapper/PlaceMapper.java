package com.tripplannertrip.mapper;

import com.tripplannertrip.entity.PlaceEntity;
import com.tripplannertrip.model.PlaceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlaceMapper {

  PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

  PlaceRecord placeEntityToPlaceRecord(PlaceEntity placeEntity);

  PlaceEntity placeRecordToPlaceEntity(PlaceRecord placeRecord);
}
