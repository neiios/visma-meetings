package com.visma.meetings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Person {
    private UUID id;
    private String name;
}
