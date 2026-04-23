package com.pragma.ms_bootcamp.infrastructure.out.adapter;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampCapacityEntity;
import com.pragma.ms_bootcamp.infrastructure.out.mapper.IBootcampEntityMapper;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampCapacityRepository;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements IBootcampPersistencePort {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final IBootcampEntityMapper bootcampEntityMapper;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcampEntityMapper.toEntity(bootcamp))
                .flatMap(saved -> {
                    List<BootcampCapacityEntity> relations = bootcamp.getCapacities()
                            .stream()
                            .map(cap -> new BootcampCapacityEntity(
                                    saved.getId(), cap.getId()
                            ))
                            .toList();

                    return bootcampCapacityRepository.saveAll(relations)
                            .collectList()
                            .thenReturn(saved);
                })
                .map(saved -> {
                    bootcamp.setId(saved.getId());
                    return bootcamp;
                });
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return bootcampRepository.existsByName(name);
    }
}
