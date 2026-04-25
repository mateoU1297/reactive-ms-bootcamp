package com.pragma.ms_bootcamp.application.handler.impl;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import com.pragma.ms_bootcamp.application.dto.PagedResponse;
import com.pragma.ms_bootcamp.application.handler.IBootcampHandler;
import com.pragma.ms_bootcamp.application.mapper.IBootcampMapper;
import com.pragma.ms_bootcamp.domain.api.IBootcampServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

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

    @Override
    public Mono<PagedResponse<BootcampResponse>> findAll(int page, int size,
                                                         String sortBy, boolean ascending) {
        return bootcampServicePort.findAll(page, size, sortBy, ascending)
                .map(paged -> new PagedResponse<>(
                        paged.getContent().stream()
                                .map(bootcampMapper::toResponse)
                                .collect(Collectors.toList()),
                        paged.getPage(),
                        paged.getSize(),
                        paged.getTotalElements(),
                        paged.getTotalPages()
                ));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return bootcampServicePort.delete(id);
    }

}
