package com.pragma.ms_bootcamp.infrastructure.out.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampReportRequest {
    private Long bootcampId;
    private String bootcampName;
    private String bootcampDescription;
    private LocalDate launchDate;
    private Integer durationMonths;
    private List<CapacityReportRequest> capacities;
}
