package com.visma.meetings.model;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@NonNull
public class Person {
    private UUID id;
    private String fullName;
}
