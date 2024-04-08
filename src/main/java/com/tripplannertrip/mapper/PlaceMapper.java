package com.tripplannertrip.mapper;

import com.tripplannertrip.model.PlaceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlaceMapper {

    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    @Mapping(target = "id", ignore = true)
    PlaceRecord tripRecordToEntity(PlaceRecord placeRecord);

    PlaceRecord tripEntityToRecord(PlaceRecord placeRecord);
}
