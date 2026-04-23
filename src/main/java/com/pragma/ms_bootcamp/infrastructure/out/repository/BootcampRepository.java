package com.pragma.ms_bootcamp.infrastructure.out.repository;

import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
    Mono<Boolean> existsByName(String name);
}
