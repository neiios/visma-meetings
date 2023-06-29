package com.visma.meetings.dto;

import com.visma.meetings.model.MeetingCategory;
import com.visma.meetings.model.MeetingType;
import com.visma.meetings.model.Person;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MeetingResponse(
        UUID id,
        String name,
        UUID responsiblePersonId,
        String description,
        MeetingCategory category,
        MeetingType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Person> participants) {
}
