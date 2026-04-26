package com.pragma.ms_bootcamp.infrastructure.config;

import com.pragma.ms_bootcamp.domain.api.IBootcampServicePort;
import com.pragma.ms_bootcamp.domain.spi.IBootcampPersistencePort;
import com.pragma.ms_bootcamp.domain.spi.ICapacityClientPort;
import com.pragma.ms_bootcamp.domain.spi.IReportClientPort;
import com.pragma.ms_bootcamp.domain.usecase.BootcampUseCase;
import com.pragma.ms_bootcamp.infrastructure.out.adapter.BootcampPersistenceAdapter;
import com.pragma.ms_bootcamp.infrastructure.out.http.CapacityWebClientAdapter;
import com.pragma.ms_bootcamp.infrastructure.out.http.ReportWebClientAdapter;
import com.pragma.ms_bootcamp.infrastructure.out.mapper.IBootcampEntityMapper;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampCapacityRepository;
import com.pragma.ms_bootcamp.infrastructure.out.repository.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final IBootcampEntityMapper bootcampEntityMapper;

    @Bean
    public WebClient capacityWebClient(@Value("${clients.capacity.url}") String capacityUrl) {
        return WebClient.builder()
                .baseUrl(capacityUrl)
                .build();
    }

    @Bean
    public WebClient reportWebClient(@Value("${clients.report.url}") String reportUrl) {
        return WebClient.builder()
                .baseUrl(reportUrl)
                .build();
    }

    @Bean
    public ICapacityClientPort capacityClientPort(WebClient capacityWebClient) {
        return new CapacityWebClientAdapter(capacityWebClient);
    }

    @Bean
    public IReportClientPort reportClientPort(WebClient reportWebClient) {
        return new ReportWebClientAdapter(reportWebClient);
    }

    @Bean
    public IBootcampPersistencePort bootcampPersistencePort(ICapacityClientPort capacityClientPort, DatabaseClient databaseClient) {
        return new BootcampPersistenceAdapter(bootcampRepository, bootcampCapacityRepository, bootcampEntityMapper,
                capacityClientPort, databaseClient
        );
    }

    @Bean
    public IBootcampServicePort bootcampServicePort(IBootcampPersistencePort bootcampPersistencePort,
                                                    ICapacityClientPort capacityClientPort, IReportClientPort reportClientPort) {
        return new BootcampUseCase(bootcampPersistencePort, capacityClientPort, reportClientPort);
    }
}
