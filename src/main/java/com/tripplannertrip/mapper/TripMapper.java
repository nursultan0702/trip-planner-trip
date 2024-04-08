package com.tripplannertrip.mapper;

import com.tripplannertrip.entity.TripEntity;
import com.tripplannertrip.model.TripRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripMapper {

    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    @Mapping(target = "id", ignore = true)
    TripEntity tripRecordToEntity(TripRecord tripRecord);

    TripRecord tripEntityToRecord(TripEntity tripEntity);
}

