package com.pragma.ms_bootcamp.infrastructure.out.adapter;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampCapacityEntity;
import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampEntity;
import com.pragma.ms_bootcamp.infrastructure.out.mapper.IBootcampEntityMapper;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampCapacityRepository;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements IBootcampPersistencePort {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final IBootcampEntityMapper bootcampEntityMapper;
    private final ICapacityClientPort capacityClientPort;
    private final DatabaseClient databaseClient;

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

    @Override
    public Mono<PagedResult<Bootcamp>> findAll(int page, int size,
                                               String sortBy, boolean ascending) {
        String direction = ascending ? "ASC" : "DESC";
        int offset = page * size;

        return bootcampRepository.count()
                .flatMap(total -> {
                    Flux<BootcampEntity> bootcamps;

                    if (sortBy.equals("name")) {
                        Sort sort = Sort.by(
                                ascending ? Sort.Direction.ASC : Sort.Direction.DESC,
                                "name"
                        );
                        bootcamps = bootcampRepository.findAllBy(PageRequest.of(page, size, sort));
                    } else {
                        String sql = """
                            SELECT b.id, b.name, b.description,
                                   b.launch_date, b.duration_months
                            FROM ms_bootcamp.bootcamp b
                            LEFT JOIN ms_bootcamp.bootcamp_capacity bc
                                ON b.id = bc.bootcamp_id
                            GROUP BY b.id, b.name, b.description,
                                     b.launch_date, b.duration_months
                            ORDER BY COUNT(bc.capacity_id) %s
                            LIMIT %d OFFSET %d
                            """.formatted(direction, size, offset);

                        bootcamps = databaseClient.sql(sql)
                                .map((row, meta) -> new BootcampEntity(
                                        row.get("id", Long.class),
                                        row.get("name", String.class),
                                        row.get("description", String.class),
                                        row.get("launch_date", LocalDate.class),
                                        row.get("duration_months", Integer.class)
                                ))
                                .all();
                    }

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
