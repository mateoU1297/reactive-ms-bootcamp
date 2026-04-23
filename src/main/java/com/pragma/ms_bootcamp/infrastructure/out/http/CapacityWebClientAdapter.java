package com.pragma.ms_bootcamp.infrastructure.out.http;

import com.pragma.ms_bootcamp.domain.exception.CapacityNotFoundException;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CapacityWebClientAdapter implements ICapacityClientPort {

    private final WebClient webClient;

    @Override
    public Mono<Capacity> findById(Long id) {
        return webClient.get()
                .uri("/api/v1/capacities/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new CapacityNotFoundException(id)))
                .bodyToMono(Capacity.class);
    }
}
