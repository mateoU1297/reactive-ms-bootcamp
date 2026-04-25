package com.pragma.ms_bootcamp.infrastructure.input.rest;

import com.pragma.ms_bootcamp.application.dto.BootcampRequest;
import com.pragma.ms_bootcamp.application.dto.BootcampResponse;
import com.pragma.ms_bootcamp.application.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
            ),
            @RouterOperation(
                    path = "/api/v1/bootcamps",
                    method = RequestMethod.GET,
                    beanClass = BootcampRestHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "findAllBootcamps",
                            summary = "List bootcamps paginated",
                            tags = {"Bootcamp"},
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sortBy", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "string",
                                                    allowableValues = {"name", "capacityCount"},
                                                    defaultValue = "name")),
                                    @Parameter(name = "ascending", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "boolean", defaultValue = "true"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            content = @Content(
                                                    schema = @Schema(implementation = PagedResponse.class)
                                            )),
                                    @ApiResponse(responseCode = "400", description = "Invalid parameters")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/bootcamps/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = BootcampRestHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteBootcamp",
                            summary = "Delete a bootcamp",
                            tags = {"Bootcamp"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Bootcamp deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Bootcamp not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampRestHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/bootcamps", handler::save)
                .GET("/api/v1/bootcamps", handler::findAll)
                .DELETE("/api/v1/bootcamps/{id}", handler::delete)
                .build();
    }
}
