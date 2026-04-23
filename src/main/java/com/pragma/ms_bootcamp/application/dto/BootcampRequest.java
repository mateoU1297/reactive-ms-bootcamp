package com.pragma.ms_bootcamp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampRequest {
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer durationMonths;
    private List<CapacityIdRequest> capacities;
}