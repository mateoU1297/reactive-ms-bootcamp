package com.pragma.ms_bootcamp.domain.usecase;

import com.pragma.ms_bootcamp.domain.exception.BootcampAlreadyExistsException;
import com.pragma.ms_bootcamp.domain.exception.BootcampNotFoundException;
import com.pragma.ms_bootcamp.domain.exception.CapacityNotFoundException;
import com.pragma.ms_bootcamp.domain.exception.DuplicateCapacityException;
import com.pragma.ms_bootcamp.domain.exception.InvalidCapacityCountException;
import com.pragma.ms_bootcamp.domain.exception.InvalidFieldException;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import com.pragma.ms_bootcamp.domain.model.PagedResult;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import com.pragma.ms_bootcamp.domain.spi.IReportClientPort;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private IBootcampPersistencePort bootcampPersistencePort;

    @Mock
    private ICapacityClientPort capacityClientPort;

    @Mock
    private IReportClientPort reportClientPort;

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
        doNothing().when(reportClientPort).notifyBootcampCreated(any());

        StepVerifier.create(bootcampUseCase.save(bootcamp))
                .expectNextMatches(b -> b.getName().equals("Bootcamp Java"))
                .verifyComplete();

        verify(reportClientPort).notifyBootcampCreated(any());
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

    @Test
    void findAll_validParams_success() {
        PagedResult<Bootcamp> paged = new PagedResult<>(
                List.of(bootcamp), 0, 10, 1L, 1
        );
        when(bootcampPersistencePort.findAll(0, 10, "name", true))
                .thenReturn(Mono.just(paged));

        StepVerifier.create(bootcampUseCase.findAll(0, 10, "name", true))
                .expectNextMatches(r -> r.getContent().size() == 1)
                .verifyComplete();
    }

    @Test
    void findAll_invalidSortBy_throwsInvalidField() {
        StepVerifier.create(bootcampUseCase.findAll(0, 10, "invalid", true))
                .expectError(InvalidFieldException.class)
                .verify();

        verifyNoInteractions(bootcampPersistencePort);
    }

    @Test
    void findAll_negativePage_throwsInvalidField() {
        StepVerifier.create(bootcampUseCase.findAll(-1, 10, "name", true))
                .expectError(InvalidFieldException.class)
                .verify();

        verifyNoInteractions(bootcampPersistencePort);
    }

    @Test
    void findAll_zeroSize_throwsInvalidField() {
        StepVerifier.create(bootcampUseCase.findAll(0, 0, "name", true))
                .expectError(InvalidFieldException.class)
                .verify();

        verifyNoInteractions(bootcampPersistencePort);
    }

    @Test
    void findAll_sortByCapacityCount_success() {
        PagedResult<Bootcamp> paged = new PagedResult<>(
                List.of(bootcamp), 0, 10, 1L, 1
        );
        when(bootcampPersistencePort.findAll(0, 10, "capacityCount", true))
                .thenReturn(Mono.just(paged));

        StepVerifier.create(bootcampUseCase.findAll(0, 10, "capacityCount", true))
                .expectNextMatches(r -> r.getContent().size() == 1)
                .verifyComplete();
    }

    @Test
    void findAll_descendingOrder_success() {
        PagedResult<Bootcamp> paged = new PagedResult<>(
                List.of(bootcamp), 0, 10, 1L, 1
        );
        when(bootcampPersistencePort.findAll(0, 10, "name", false))
                .thenReturn(Mono.just(paged));

        StepVerifier.create(bootcampUseCase.findAll(0, 10, "name", false))
                .expectNextMatches(r -> r.getContent().size() == 1)
                .verifyComplete();
    }

    @Test
    void delete_existingBootcamp_success() {
        when(bootcampPersistencePort.existsById(1L)).thenReturn(Mono.just(true));
        when(bootcampPersistencePort.delete(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.delete(1L))
                .verifyComplete();
    }

    @Test
    void delete_notFound_throwsBootcampNotFound() {
        when(bootcampPersistencePort.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(bootcampUseCase.delete(1L))
                .expectError(BootcampNotFoundException.class)
                .verify();

        verify(bootcampPersistencePort, never()).delete(any());
    }

    @Test
    void findById_existingBootcamp_success() {
        when(bootcampPersistencePort.findById(1L)).thenReturn(Mono.just(bootcamp));

        StepVerifier.create(bootcampUseCase.findById(1L))
                .expectNextMatches(b -> b.getName().equals("Bootcamp Java"))
                .verifyComplete();
    }

    @Test
    void findById_notFound_throwsBootcampNotFound() {
        when(bootcampPersistencePort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.findById(1L))
                .expectError(BootcampNotFoundException.class)
                .verify();
    }

    @Test
    void findById_notFound_neverCallsSave() {
        when(bootcampPersistencePort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bootcampUseCase.findById(1L))
                .expectError(BootcampNotFoundException.class)
                .verify();

        verify(bootcampPersistencePort, never()).save(any());
    }
}
