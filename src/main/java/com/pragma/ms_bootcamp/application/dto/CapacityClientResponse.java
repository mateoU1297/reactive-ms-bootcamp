package com.pragma.ms_bootcamp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityClientResponse {
    private Long id;
    private String name;
    private List<TechnologyClientResponse> technologies;
}
