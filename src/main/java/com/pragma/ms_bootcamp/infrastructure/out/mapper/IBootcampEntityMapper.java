package com.pragma.ms_bootcamp.infrastructure.out.mapper;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IBootcampEntityMapper {
    BootcampEntity toEntity(Bootcamp bootcamp);
    Bootcamp toDomain(BootcampEntity entity);
}
