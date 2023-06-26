package com.visma.meetings.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PersonDTO(
        UUID id,
        String name) {
}
