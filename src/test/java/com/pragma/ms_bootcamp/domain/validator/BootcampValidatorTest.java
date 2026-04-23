package com.pragma.ms_bootcamp.domain.validator;

import com.pragma.ms_bootcamp.domain.exception.DuplicateCapacityException;
import com.pragma.ms_bootcamp.domain.exception.InvalidCapacityCountException;
import com.pragma.ms_bootcamp.domain.exception.InvalidFieldException;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

class BootcampValidatorTest {

    private Bootcamp validBootcamp;

    @BeforeEach
    void setUp() {
        validBootcamp = new Bootcamp(null, "Bootcamp Java", "Description",
                LocalDate.now().plusDays(1), 6,
                List.of(new Capacity(1L, "Backend")));
    }

    @Test
    void validate_validBootcamp_success() {
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validate_nullName_throwsInvalidField() {
        validBootcamp.setName(null);
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_nameTooLong_throwsInvalidField() {
        validBootcamp.setName("A".repeat(51));
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_nullDescription_throwsInvalidField() {
        validBootcamp.setDescription(null);
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_nullLaunchDate_throwsInvalidField() {
        validBootcamp.setLaunchDate(null);
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_zeroDuration_throwsInvalidField() {
        validBootcamp.setDurationMonths(0);
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_negativeDuration_throwsInvalidField() {
        validBootcamp.setDurationMonths(-1);
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_noCapacities_throwsInvalidCount() {
        validBootcamp.setCapacities(List.of());
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidCapacityCountException.class)
                .verify();
    }

    @Test
    void validate_moreThanMaxCapacities_throwsInvalidCount() {
        validBootcamp.setCapacities(List.of(
                new Capacity(1L, "A"), new Capacity(2L, "B"),
                new Capacity(3L, "C"), new Capacity(4L, "D"),
                new Capacity(5L, "E")
        ));
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(InvalidCapacityCountException.class)
                .verify();
    }

    @Test
    void validate_duplicateCapacities_throwsDuplicate() {
        validBootcamp.setCapacities(List.of(
                new Capacity(1L, "Backend"),
                new Capacity(1L, "Backend")
        ));
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectError(DuplicateCapacityException.class)
                .verify();
    }

    @Test
    void validate_exactlyMaxCapacities_success() {
        validBootcamp.setCapacities(List.of(
                new Capacity(1L, "A"), new Capacity(2L, "B"),
                new Capacity(3L, "C"), new Capacity(4L, "D")
        ));
        StepVerifier.create(BootcampValidator.validate(validBootcamp))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validatePagination_validParams_success() {
        StepVerifier.create(BootcampValidator.validatePagination(0, 10, "name"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_sortByCapacityCount_success() {
        StepVerifier.create(BootcampValidator.validatePagination(0, 10, "capacityCount"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_negativePage_throwsInvalidField() {
        StepVerifier.create(BootcampValidator.validatePagination(-1, 10, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Page must be >= 0"))
                .verify();
    }

    @Test
    void validatePagination_zeroSize_throwsInvalidField() {
        StepVerifier.create(BootcampValidator.validatePagination(0, 0, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Size must be > 0"))
                .verify();
    }

    @Test
    void validatePagination_negativeSize_throwsInvalidField() {
        StepVerifier.create(BootcampValidator.validatePagination(0, -1, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Size must be > 0"))
                .verify();
    }

    @Test
    void validatePagination_invalidSortBy_throwsInvalidField() {
        StepVerifier.create(BootcampValidator.validatePagination(0, 10, "invalid"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().contains("SortBy must be one of"))
                .verify();
    }
}
