package com.pragma.ms_bootcamp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer durationMonths;
    private List<CapacityResponse> capacities;
}
