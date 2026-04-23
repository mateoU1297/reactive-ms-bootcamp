package com.pragma.ms_bootcamp.application.handler;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import reactor.core.publisher.Mono;

public interface IBootcampHandler {
    Mono<BootcampResponse> save(BootcampRequest request);
}
