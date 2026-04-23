package com.pragma.ms_bootcamp.infrastructure.out.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("bootcamp_capacity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampCapacityEntity {

    private Long bootcampId;

    private Long capacityId;
}