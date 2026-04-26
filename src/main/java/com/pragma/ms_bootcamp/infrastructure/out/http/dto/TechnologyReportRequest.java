package com.pragma.ms_bootcamp.infrastructure.out.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyReportRequest {
    private Long id;
    private String name;
}