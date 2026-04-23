package com.pragma.ms_bootcamp.domain.model;

import java.time.LocalDate;
import java.util.List;

public class Bootcamp {
    private Long id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer durationMonths;
    private List<Capacity> capacities;

    public Bootcamp() {
    }

    public Bootcamp(Long id, String name, String description, LocalDate launchDate, Integer durationMonths, List<Capacity> capacities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.launchDate = launchDate;
        this.durationMonths = durationMonths;
        this.capacities = capacities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public List<Capacity> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Capacity> capacities) {
        this.capacities = capacities;
    }
}
