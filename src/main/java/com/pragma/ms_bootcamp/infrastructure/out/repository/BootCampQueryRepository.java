package com.pragma.ms_bootcamp.infrastructure.out.repository;

import com.pragma.ms_bootcamp.infrastructure.out.entity.BootcampEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BootCampQueryRepository {

    private final DatabaseClient databaseClient;

    private static final String FIND_ALL_ORDER_BY_NAME = """
            SELECT id, name, description, launch_date, duration_months
            FROM ms_bootcamp.bootcamp
            ORDER BY name %s
            LIMIT %d OFFSET %d
            """;

    private static final String FIND_ALL_ORDER_BY_CAPACITY_COUNT = """
            SELECT b.id, b.name, b.description, b.launch_date, b.duration_months
            FROM ms_bootcamp.bootcamp b
            LEFT JOIN ms_bootcamp.bootcamp_capacity bc
                ON b.id = bc.bootcamp_id
            GROUP BY b.id, b.name, b.description, b.launch_date, b.duration_months
            ORDER BY COUNT(bc.capacity_id) %s
            LIMIT %d OFFSET %d
            """;

    public Flux<BootcampEntity> findAllOrderByName(String direction, int size, int offset) {
        return databaseClient.sql(FIND_ALL_ORDER_BY_NAME.formatted(direction, size, offset))
                .map((row, meta) -> new BootcampEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("launch_date", LocalDate.class),
                        row.get("duration_months", Integer.class)
                ))
                .all();
    }

    public Flux<BootcampEntity> findAllOrderByCapacityCount(String direction,
                                                            int size, int offset) {
        return databaseClient.sql(
                FIND_ALL_ORDER_BY_CAPACITY_COUNT.formatted(direction, size, offset))
                .map((row, meta) -> new BootcampEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("launch_date", LocalDate.class),
                        row.get("duration_months", Integer.class)
                ))
                .all();
    }
}
