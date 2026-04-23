package com.pragma.ms_bootcamp.infrastructure.input.rest;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/bootcamps",
                    method = RequestMethod.POST,
                    beanClass = BootcampRestHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "saveBootcamp",
                            summary = "Register a new bootcamp",
                            tags = {"Bootcamp"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = BootcampRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201",
                                            content = @Content(
                                                    schema = @Schema(implementation = BootcampResponse.class)
                                            )),
                                    @ApiResponse(responseCode = "400",
                                            description = "Invalid field"),
                                    @ApiResponse(responseCode = "404",
                                            description = "Capacity not found"),
                                    @ApiResponse(responseCode = "409",
                                            description = "Bootcamp already exists")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampRestHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/bootcamps", handler::save)
                .build();
    }
}
