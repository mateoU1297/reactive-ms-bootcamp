package com.pragma.ms_bootcamp.domain.model;

public class Capacity {
    private Long id;
    private String name;

    public Capacity() {
    }

    public Capacity(Long id, String name) {
        this.id = id;
        this.name = name;
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
}