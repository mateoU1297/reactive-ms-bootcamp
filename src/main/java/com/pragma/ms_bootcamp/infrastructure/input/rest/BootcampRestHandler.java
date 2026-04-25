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

    public Mono<ServerResponse> findAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        boolean ascending = Boolean.parseBoolean(request.queryParam("ascending").orElse("true"));

        return bootcampHandler.findAll(page, size, sortBy, ascending)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return bootcampHandler.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
