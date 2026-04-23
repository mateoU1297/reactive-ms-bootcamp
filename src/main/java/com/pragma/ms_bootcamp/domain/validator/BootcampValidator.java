package com.pragma.ms_bootcamp.domain.validator;

import com.pragma.ms_bootcamp.domain.exception.DuplicateCapacityException;
import com.pragma.ms_bootcamp.domain.exception.InvalidCapacityCountException;
import com.pragma.ms_bootcamp.domain.exception.InvalidFieldException;
import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.model.Capacity;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public class BootcampValidator {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 90;
    private static final int MIN_CAPACITIES = 1;
    private static final int MAX_CAPACITIES = 4;

    private BootcampValidator() {
    }

    public static Mono<Bootcamp> validate(Bootcamp bootcamp) {
        return validateName(bootcamp.getName())
                .then(validateDescription(bootcamp.getDescription()))
                .then(validateLaunchDate(bootcamp.getLaunchDate()))
                .then(validateDuration(bootcamp.getDurationMonths()))
                .then(validateCapacities(bootcamp.getCapacities()))
                .thenReturn(bootcamp);
    }

    private static Mono<Void> validateName(String name) {
        if (name == null || name.isBlank())
            return Mono.error(new InvalidFieldException("Name is required"));
        if (name.length() > MAX_NAME_LENGTH)
            return Mono.error(new InvalidFieldException(
                    "Name must not exceed " + MAX_NAME_LENGTH + " characters"));
        return Mono.empty();
    }

    private static Mono<Void> validateDescription(String description) {
        if (description == null || description.isBlank())
            return Mono.error(new InvalidFieldException("Description is required"));
        if (description.length() > MAX_DESCRIPTION_LENGTH)
            return Mono.error(new InvalidFieldException(
                    "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters"));
        return Mono.empty();
    }

    private static Mono<Void> validateLaunchDate(LocalDate launchDate) {
        if (launchDate == null)
            return Mono.error(new InvalidFieldException("Launch date is required"));
        return Mono.empty();
    }

    private static Mono<Void> validateDuration(Integer durationMonths) {
        if (durationMonths == null || durationMonths <= 0)
            return Mono.error(new InvalidFieldException(
                    "Duration must be greater than 0"));
        return Mono.empty();
    }

    private static Mono<Void> validateCapacities(List<Capacity> capacities) {
        if (capacities == null || capacities.size() < MIN_CAPACITIES)
            return Mono.error(new InvalidCapacityCountException(
                    "Bootcamp must have at least " + MIN_CAPACITIES + " capacity"));
        if (capacities.size() > MAX_CAPACITIES)
            return Mono.error(new InvalidCapacityCountException(
                    "Bootcamp must have at most " + MAX_CAPACITIES + " capacities"));

        long distinctCount = capacities.stream()
                .map(Capacity::getId)
                .distinct()
                .count();
        if (distinctCount < capacities.size())
            return Mono.error(new DuplicateCapacityException());

        return Mono.empty();
    }
}