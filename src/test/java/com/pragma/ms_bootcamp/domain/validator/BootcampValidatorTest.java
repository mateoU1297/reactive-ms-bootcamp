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
}
