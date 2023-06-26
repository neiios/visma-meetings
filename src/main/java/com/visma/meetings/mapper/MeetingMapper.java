package com.visma.meetings.mapper;

import com.visma.meetings.dto.MeetingDTO;
import com.visma.meetings.model.Meeting;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

@Service
public class MeetingMapper implements Function<MeetingDTO, Meeting> {
    @Override
    public Meeting apply(MeetingDTO meetingDTO) {
        return new Meeting(
                UUID.randomUUID(),
                meetingDTO.name(),
                meetingDTO.responsiblePersonId(),
                meetingDTO.description(),
                meetingDTO.category(),
                meetingDTO.type(),
                meetingDTO.startDate(),
                meetingDTO.endDate(),
                meetingDTO.participants()
        );
    }
}
