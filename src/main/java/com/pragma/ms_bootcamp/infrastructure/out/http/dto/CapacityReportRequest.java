package com.pragma.ms_bootcamp.infrastructure.out.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityReportRequest {
    private Long id;
    private String name;
    private List<TechnologyReportRequest> technologies;
}
