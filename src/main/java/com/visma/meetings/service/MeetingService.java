package com.visma.meetings.service;

import com.visma.meetings.dto.MeetingDTO;
import com.visma.meetings.dto.PersonDTO;
import com.visma.meetings.exception.RequestValidationException;
import com.visma.meetings.exception.ResourceNotFoundException;
import com.visma.meetings.mapper.MeetingMapper;
import com.visma.meetings.mapper.PersonMapper;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.MeetingCategory;
import com.visma.meetings.model.MeetingType;
import com.visma.meetings.model.Person;
import com.visma.meetings.repository.MeetingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper = new MeetingMapper();
    private final PersonMapper personMapper = new PersonMapper();

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public void addMeeting(MeetingDTO meetingDTO) {
        Meeting meeting = meetingMapper.apply(meetingDTO);
        meetingRepository.addMeeting(meeting);
    }

    public void deleteMeeting(UUID meetingId, UUID requesterId) {
        if (personIsResponsibleForMeeting(meetingId, requesterId)) {
            meetingRepository.deleteMeeting(meetingId);
        }
    }

    public List<Meeting> getMeetings(
            String containsInDescription,
            UUID responsiblePersonId,
            MeetingCategory category,
            MeetingType type,
            LocalDateTime afterDate,
            LocalDateTime beforeDate,
            Integer numberOfAttendees) {
        Stream<Meeting> meetings = meetingRepository.getMeetings().stream();

        if (containsInDescription != null) {
            // filtering must be case-insensitive
            meetings = meetings
                    .filter(meeting ->
                            meeting.getDescription().toLowerCase().contains(containsInDescription.toLowerCase()));
        }

        if (responsiblePersonId != null) {
            meetings = meetings
                    .filter(meeting ->
                            meeting.getResponsiblePersonId().equals(responsiblePersonId));
        }

        if (category != null) {
            meetings = meetings
                    .filter(meeting ->
                            meeting.getCategory().equals(category));
        }

        if (type != null) {
            meetings = meetings
                    .filter(meeting ->
                            meeting.getType().equals(type));
        }

        if (numberOfAttendees != null) {
            meetings = meetings
                    .filter(meeting ->
                            meeting.getParticipants().size() > numberOfAttendees);
        }

        // Both can be not-null and create a window effect
        if (beforeDate != null) {
            meetings = meetings.filter(meeting -> meeting.getEndDate().isBefore(beforeDate));
        }
        if (afterDate != null) {
            meetings = meetings.filter(meeting -> meeting.getStartDate().isAfter(afterDate));
        }

        return meetings.toList();
    }

    public ResponseEntity<String> addPersonToMeeting(UUID meetingId, PersonDTO personDTO) {
        Person person = personMapper.apply(personDTO);

        Meeting requestedMeeting = findMeetingById(meetingId).orElseThrow(() ->
                new ResourceNotFoundException("Meeting with ID [%s] does not exist.".formatted(meetingId)));

        if (personIsAlreadyInSameMeeting(requestedMeeting, person.getId())) {
            throw new RequestValidationException("The user you are trying to add is already present in the same meeting.");
        }

        if (personIsBusy(person.getId(), requestedMeeting.getStartDate(), requestedMeeting.getEndDate())) {
            return ResponseEntity.ok("Participant has overlapping meetings.");
        }

        return ResponseEntity.ok("The time participant was added is " + LocalDateTime.now());
    }

    public void removePersonFromMeeting(UUID meetingId, UUID personId) {
        if (personIsResponsibleForMeeting(meetingId, personId)) {
            throw new RequestValidationException("Requested person can't be removed as they are responsible for the meeting.");
        }

        meetingRepository.removePersonFromMeeting(meetingId, personId);
    }

    private boolean personIsBusy(UUID id, LocalDateTime newMeetingStartDate, LocalDateTime newMeetingEndDate) {
        var meetings = meetingRepository.getMeetings();

        var meetingsPersonIsIn = meetings.stream()
                .filter(meeting ->
                        meeting.getParticipants().stream()
                                .anyMatch(participant -> participant.getId().equals(id)))
                .toList();

        return meetingsPersonIsIn.stream().anyMatch(meeting ->
                meeting.getStartDate().isBefore(newMeetingEndDate) &&
                        newMeetingStartDate.isBefore(meeting.getStartDate()));
    }

    private boolean personIsResponsibleForMeeting(UUID meetingId, UUID personId) {
        Optional<Meeting> requestedMeeting = findMeetingById(meetingId);

        return requestedMeeting
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting with ID [%s] does not exist.".formatted(meetingId)))
                .getResponsiblePersonId()
                .equals(personId);
    }

    private Optional<Meeting> findMeetingById(UUID meetingId) {
        List<Meeting> meetings = meetingRepository.getMeetings();

        return meetings.stream()
                .filter(meeting -> meeting.getId().equals(meetingId))
                .findAny();
    }

    private boolean personIsAlreadyInSameMeeting(Meeting requestedMeeting, UUID id) {
        return requestedMeeting.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(id));
    }
}
