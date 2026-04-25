package com.pragma.ms_bootcamp.domain.usecase;

import com.pragma.ms_bootcamp.domain.api.IBootcampServicePort;
import com.pragma.ms_bootcamp.domain.exception.BootcampAlreadyExistsException;
import com.pragma.ms_bootcamp.domain.exception.BootcampNotFoundException;
import com.pragma.ms_bootcamp.domain.exception.CapacityNotFoundException;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import com.pragma.ms_bootcamp.domain.validator.BootcampValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class BootcampUseCase implements IBootcampServicePort {

    private final IBootcampPersistencePort bootcampPersistencePort;
    private final ICapacityClientPort capacityClientPort;

    public BootcampUseCase(IBootcampPersistencePort bootcampPersistencePort, ICapacityClientPort capacityClientPort) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacityClientPort = capacityClientPort;
    }

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return BootcampValidator.validate(bootcamp)
                .flatMap(b -> bootcampPersistencePort.existsByName(b.getName()))
                .flatMap(exists -> {
                    if (exists)
                        return Mono.error(
                                new BootcampAlreadyExistsException(bootcamp.getName())
                        );
                    return validateCapacitiesExist(bootcamp.getCapacities());
                })
                .flatMap(capacities -> {
                    bootcamp.setCapacities(capacities);
                    return bootcampPersistencePort.save(bootcamp);
                });
    }

    @Override
    public Mono<PagedResult<Bootcamp>> findAll(int page, int size, String sortBy, boolean ascending) {
        return BootcampValidator.validatePagination(page, size, sortBy)
                .flatMap(valid -> bootcampPersistencePort.findAll(page, size, sortBy, ascending));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return bootcampPersistencePort.existsById(id)
                .flatMap(exists -> {
                    if (!exists)
                        return Mono.error(new BootcampNotFoundException(id));
                    return bootcampPersistencePort.delete(id);
                });
    }

    private Mono<List<Capacity>> validateCapacitiesExist(List<Capacity> capacities) {
        return Flux.fromIterable(capacities)
                .flatMap(cap -> capacityClientPort.findById(cap.getId())
                        .switchIfEmpty(Mono.error(
                                new CapacityNotFoundException(cap.getId())
                        ))
                )
                .collectList();
    }
}
