package com.pragma.ms_bootcamp.domain.spi;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import reactor.core.publisher.Mono;

public interface IBootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);

    Mono<Boolean> existsByName(String name);

    Mono<PagedResult<Bootcamp>> findAll(int page, int size, String sortBy, boolean ascending);

    Mono<Void> delete(Long id);

    Mono<Boolean> existsById(Long id);
}
