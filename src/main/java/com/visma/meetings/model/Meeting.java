package com.visma.meetings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Meeting {
    private UUID id;
    private String name;
    private UUID responsiblePersonId;
    private String description;
    private MeetingCategory category;
    private MeetingType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Person> participants = new ArrayList<>();
}

