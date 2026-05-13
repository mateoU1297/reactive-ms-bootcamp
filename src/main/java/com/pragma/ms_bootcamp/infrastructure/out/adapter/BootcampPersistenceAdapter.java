package com.pragma.ms_bootcamp.infrastructure.out.adapter;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampCapacityEntity;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampEntity;
import com.pragma.ms_bootcamp.infrastructure.out.mapper.IBootcampEntityMapper;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootCampQueryRepository;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampCapacityRepository;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements IBootcampPersistencePort {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final IBootcampEntityMapper bootcampEntityMapper;
    private final ICapacityClientPort capacityClientPort;
    private final BootCampQueryRepository bootcampQueryRepository;

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
                            .then()
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

    @Override
    public Mono<PagedResult<Bootcamp>> findAll(int page, int size, String sortBy, boolean ascending) {
        String direction = ascending ? "ASC" : "DESC";
        int offset = page * size;

        return bootcampRepository.count()
                .flatMap(total -> {
                    Flux<BootcampEntity> bootcamps;

                    if (sortBy.equals("name"))
                        bootcamps = bootcampQueryRepository.findAllOrderByName(direction, size, offset);
                    else
                        bootcamps = bootcampQueryRepository.findAllOrderByCapacityCount(direction, size, offset);

                    return bootcamps
                            .flatMap(this::mapWithCapacities)
                            .collectList()
                            .map(list -> new PagedResult<>(
                                    list,
                                    page,
                                    size,
                                    total,
                                    (int) Math.ceil((double) total / size)
                            ));
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return bootcampRepository.existsById(id);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        return bootcampCapacityRepository.findByBootcampId(id)
                .flatMap(rel ->
                        bootcampCapacityRepository.countByCapacityId(rel.getCapacityId())
                                .flatMap(count -> {
                                    if (count <= 1)
                                        return capacityClientPort.deleteIfNotReferenced(rel.getCapacityId());

                                    return Mono.empty();
                                })
                )
                .then(bootcampCapacityRepository.deleteByBootcampId(id))
                .then(bootcampRepository.deleteById(id));
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        return bootcampRepository.findById(id)
                .map(bootcampEntityMapper::toDomain);
    }

    private Mono<Bootcamp> mapWithCapacities(BootcampEntity entity) {
        return bootcampCapacityRepository.findByBootcampId(entity.getId())
                .flatMap(rel -> capacityClientPort.findById(rel.getCapacityId()))
                .collectList()
                .map(capacities -> {
                    Bootcamp bootcamp = bootcampEntityMapper.toDomain(entity);
                    bootcamp.setCapacities(capacities);
                    return bootcamp;
                });
    }
}
