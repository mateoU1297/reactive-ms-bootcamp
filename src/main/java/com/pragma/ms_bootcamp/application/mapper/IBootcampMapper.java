package com.pragma.ms_bootcamp.application.mapper;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import com.pragma.ms_bootcamp.application.dto.CapacityIdRequest;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IBootcampMapper {
    Bootcamp toDomain(BootcampRequest request);
    BootcampResponse toResponse(Bootcamp bootcamp);

    default List<Capacity> mapCapacityRequests(List<CapacityIdRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .map(req -> new Capacity(req.getId(), null))
                .collect(Collectors.toList());
    }
}
