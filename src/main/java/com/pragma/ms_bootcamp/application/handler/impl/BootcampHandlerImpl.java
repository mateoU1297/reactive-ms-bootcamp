package com.pragma.ms_bootcamp.application.handler.impl;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import com.pragma.ms_bootcamp.application.handler.IBootcampHandler;
import com.pragma.ms_bootcamp.application.mapper.IBootcampMapper;
import com.pragma.ms_bootcamp.domain.api.IBootcampServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BootcampHandlerImpl implements IBootcampHandler {

    private final IBootcampServicePort bootcampServicePort;
    private final IBootcampMapper bootcampMapper;

    @Override
    public Mono<BootcampResponse> save(BootcampRequest request) {
        return bootcampServicePort.save(bootcampMapper.toDomain(request))
                .map(bootcampMapper::toResponse);
    }
}
