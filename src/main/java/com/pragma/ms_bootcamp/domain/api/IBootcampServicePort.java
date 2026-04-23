package com.pragma.ms_bootcamp.domain.api;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import reactor.core.publisher.Mono;

public interface IBootcampServicePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);

    Mono<PagedResult<Bootcamp>> findAll(int page, int size, String sortBy, boolean ascending);
}
