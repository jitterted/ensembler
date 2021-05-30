package com.jitterted.moborg.adapter.out.mobtimer;

import java.util.UUID;

public class PersonDto {
    private final String id;
    private final String name;

    public PersonDto(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString().substring(0, 10);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
