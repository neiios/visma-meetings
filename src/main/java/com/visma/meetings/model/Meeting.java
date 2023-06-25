package com.visma.meetings.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Meeting {
    private UUID id;
    private String name;
    private UUID responsiblePersonId;
    private String description;
    private MeetingCategory category;
    private MeetingType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Person> participants;
}

