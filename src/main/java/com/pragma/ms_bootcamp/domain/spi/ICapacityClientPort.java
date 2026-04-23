package com.pragma.ms_bootcamp.domain.spi;

import com.pragma.ms_bootcamp.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityClientPort {
    Mono<Capacity> findById(Long id);
}
