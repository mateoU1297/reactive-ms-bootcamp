package com.pragma.ms_bootcamp.infrastructure.out.http;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;
import com.pragma.ms_bootcamp.domain.spi.IReportClientPort;
import com.pragma.ms_bootcamp.infrastructure.out.http.dto.BootcampReportRequest;
import com.pragma.ms_bootcamp.infrastructure.out.http.dto.CapacityReportRequest;
import com.pragma.ms_bootcamp.infrastructure.out.http.dto.TechnologyReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ReportWebClientAdapter implements IReportClientPort {

    private final WebClient webClient;

    @Override
    public void notifyBootcampCreated(Bootcamp bootcamp) {
        webClient.post()
                .uri("/api/v1/reports/bootcamps")
                .bodyValue(toRequest(bootcamp))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("Error notifying report service: {}", e.getMessage()))
                .subscribe();
    }

    private BootcampReportRequest toRequest(Bootcamp bootcamp) {
        List<CapacityReportRequest> capacities = bootcamp.getCapacities() == null
                ? List.of()
                : bootcamp.getCapacities().stream()
                  .map(c -> new CapacityReportRequest(
                          c.getId(),
                          c.getName(),
                          c.getTechnologies() == null ? List.of()
                          : c.getTechnologies().stream()
                            .map(t -> new TechnologyReportRequest(
                                    t.getId(), t.getName()
                            ))
                            .collect(Collectors.toList())
                  ))
                  .collect(Collectors.toList());

        return new BootcampReportRequest(
                bootcamp.getId(),
                bootcamp.getName(),
                bootcamp.getDescription(),
                bootcamp.getLaunchDate(),
                bootcamp.getDurationMonths(),
                capacities
        );
    }
}