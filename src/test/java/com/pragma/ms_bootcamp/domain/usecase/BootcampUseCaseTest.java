package com.pragma.ms_bootcamp.domain.usecase;

import com.pragma.ms_bootcamp.domain.exception.BootcampAlreadyExistsException;
import com.pragma.ms_bootcamp.domain.exception.CapacityNotFoundException;
import com.pragma.ms_bootcamp.domain.exception.DuplicateCapacityException;
import com.pragma.ms_bootcamp.domain.exception.InvalidCapacityCountException;
import com.pragma.ms_bootcamp.domain.exception.InvalidFieldException;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private IBootcampPersistencePort bootcampPersistencePort;

    @Mock
    private ICapacityClientPort capacityClientPort;

    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    private Bootcamp bootcamp;
    private List<Capacity> capacities;

    @BeforeEach
    void setUp() {
        capacities = List.of(new Capacity(1L, "Backend"));
        bootcamp = new Bootcamp(null, "Bootcamp Java", "Description",
                LocalDate.now().plusDays(1), 6, capacities);
    }

    @Test
    void save_validBootcamp_success() {
        when(bootcampPersistencePort.existsByName("Bootcamp Java"))
                .thenReturn(Mono.just(false));
        when(capacityClientPort.findById(1L))
                .thenReturn(Mono.just(capacities.get(0)));
        when(bootcampPersistencePort.save(any()))
                .thenReturn(Mono.just(bootcamp));

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectNextMatches(b -> b.getName().equals("Bootcamp Java"))
                .verifyComplete();
    }

    @Test
    void save_nameExists_throwsAlreadyExists() {
        when(bootcampPersistencePort.existsByName("Bootcamp Java"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(BootcampAlreadyExistsException.class)
                .verify();
    }

    @Test
    void save_capacityNotFound_throwsNotFound() {
        when(bootcampPersistencePort.existsByName("Bootcamp Java"))
                .thenReturn(Mono.just(false));
        when(capacityClientPort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(CapacityNotFoundException.class)
                .verify();
    }

    @Test
    void save_duplicateCapacities_throwsDuplicate() {
        bootcamp.setCapacities(List.of(
                new Capacity(1L, "Backend"),
                new Capacity(1L, "Backend")
        ));

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(DuplicateCapacityException.class)
                .verify();
    }

    @Test
    void save_noCapacities_throwsInvalidCount() {
        bootcamp.setCapacities(List.of());

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(InvalidCapacityCountException.class)
                .verify();
    }

    @Test
    void save_moreThanMaxCapacities_throwsInvalidCount() {
        bootcamp.setCapacities(List.of(
                new Capacity(1L, "A"),
                new Capacity(2L, "B"),
                new Capacity(3L, "C"),
                new Capacity(4L, "D"),
                new Capacity(5L, "E")
        ));

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(InvalidCapacityCountException.class)
                .verify();
    }

    @Test
    void save_exactlyMaxCapacities_success() {
        bootcamp.setCapacities(List.of(
                new Capacity(1L, "A"),
                new Capacity(2L, "B"),
                new Capacity(3L, "C"),
                new Capacity(4L, "D")
        ));
        when(bootcampPersistencePort.existsByName(any())).thenReturn(Mono.just(false));
        when(capacityClientPort.findById(any())).thenReturn(Mono.just(capacities.get(0)));
        when(bootcampPersistencePort.save(any())).thenReturn(Mono.just(bootcamp));

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_nullName_throwsInvalidField() {
        bootcamp.setName(null);

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void save_nullLaunchDate_throwsInvalidField() {
        bootcamp.setLaunchDate(null);

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void save_zeroDuration_throwsInvalidField() {
        bootcamp.setDurationMonths(0);

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }
}
