package com.pragma.ms_bootcamp.infrastructure.out.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("bootcamp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampEntity {

    @Id
    private Long id;

    private String name;

    private String description;

    private LocalDate launchDate;

    private Integer durationMonths;
}
