package com.visma.meetings.service;

import com.visma.meetings.dto.MeetingCreationRequest;
import com.visma.meetings.exception.RequestValidationException;
import com.visma.meetings.exception.ResourceNotFoundException;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.Person;
import com.visma.meetings.repository.MeetingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public void addMeeting(MeetingCreationRequest meetingCreationRequest) {
        Meeting meeting = new Meeting(
                UUID.randomUUID(),
                meetingCreationRequest.name(),
                meetingCreationRequest.responsiblePersonId(),
                meetingCreationRequest.description(),
                meetingCreationRequest.category(),
                meetingCreationRequest.type(),
                meetingCreationRequest.startDate(),
                meetingCreationRequest.endDate(),
                meetingCreationRequest.participants());

        meetingRepository.addMeeting(meeting);
    }

    public void deleteMeeting(UUID meetingId, UUID requesterId) {
        if (personIsResponsibleForMeeting(meetingId, requesterId)) {
            meetingRepository.deleteMeeting(meetingId);
        }
    }

    public List<Meeting> getMeetings() {
        return meetingRepository.getMeetings();
    }

    public ResponseEntity<String> addPersonToMeeting(UUID meetingId, Person person) {
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

    public void removePersonFromMeeting(UUID meetingId, UUID personId) {
        if (!personIsResponsibleForMeeting(meetingId, personId)) {
            meetingRepository.removePersonFromMeeting(meetingId, personId);
        }
    }

    private boolean personIsResponsibleForMeeting(UUID meetingId, UUID personId) {
        Optional<Meeting> requestedMeeting = findMeetingById(meetingId);

        return requestedMeeting
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting with ID [%s] does not exist.".formatted(meetingId)))
                .getResponsiblePersonId().equals(personId);
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
