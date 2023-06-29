package com.visma.meetings.mapper;

import com.visma.meetings.dto.MeetingResponse;
import com.visma.meetings.dto.MeetingRequest;
import com.visma.meetings.model.Meeting;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@UtilityClass
public class MeetingMapper {
    public static Meeting requestToMeeting(MeetingRequest meetingRequest) {
        return new Meeting(
                UUID.randomUUID(),
                meetingRequest.name(),
                meetingRequest.responsiblePersonId(),
                meetingRequest.description(),
                meetingRequest.category(),
                meetingRequest.type(),
                meetingRequest.startDate(),
                meetingRequest.endDate(),
                meetingRequest.participants()
        );
    }

    public static MeetingResponse meetingToResponse(Meeting meeting) {
        return new MeetingResponse(
                meeting.getId(),
                meeting.getName(),
                meeting.getResponsiblePersonId(),
                meeting.getDescription(),
                meeting.getCategory(),
                meeting.getType(),
                meeting.getStartDate(),
                meeting.getEndDate(),
                meeting.getParticipants()
        );
    }
}
