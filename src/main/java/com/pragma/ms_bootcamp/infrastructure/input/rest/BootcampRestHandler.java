package com.pragma.ms_bootcamp.infrastructure.input.rest;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.handler.IBootcampHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BootcampRestHandler {

    private final IBootcampHandler bootcampHandler;

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(BootcampRequest.class)
                .flatMap(bootcampHandler::save)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }
}
