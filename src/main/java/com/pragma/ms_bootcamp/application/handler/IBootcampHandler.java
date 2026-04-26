package com.pragma.ms_bootcamp.application.handler;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import com.pragma.ms_bootcamp.application.dto.PagedResponse;
import reactor.core.publisher.Mono;

public interface IBootcampHandler {
    Mono<BootcampResponse> save(BootcampRequest request);

    Mono<PagedResponse<BootcampResponse>> findAll(int page, int size, String sortBy, boolean ascending);

    Mono<Void> delete(Long id);

    Mono<BootcampResponse> findById(Long id);
}
