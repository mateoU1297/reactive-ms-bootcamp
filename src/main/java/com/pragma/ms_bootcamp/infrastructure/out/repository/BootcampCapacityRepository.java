package com.pragma.ms_bootcamp.infrastructure.out.repository;

import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampCapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampCapacityRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long> {
    Flux<BootcampCapacityEntity> findByBootcampId(Long bootcampId);

    Mono<Void> deleteByBootcampId(Long bootcampId);

    Mono<Long> countByCapacityId(Long capacityId);
}
