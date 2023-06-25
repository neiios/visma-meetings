package com.visma.meetings.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@Slf4j
public class MeetingRepository {
    private final List<Meeting> meetings;
    private final ObjectMapper objectMapper;

    public MeetingRepository(ObjectMapper objectMapper) {
        List<Meeting> tempMeetings;
        this.objectMapper = new ObjectMapper().findAndRegisterModules()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            tempMeetings = objectMapper.readValue(new File("src/main/resources/db.json"), new TypeReference<List<Meeting>>() {
            });
        } catch (IOException e) {
            log.error(String.format("Failed to read saved meetings. Error: %s", e.getMessage()));
            tempMeetings = new ArrayList<>();
        }

        meetings = tempMeetings;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void deleteMeeting(UUID id) {
        meetings.removeIf(meeting -> Objects.equals(meeting.getId(), id));
        saveStateToDatabase();
    }

    public void addMeeting(Meeting meeting) {
        meetings.add(meeting);
        saveStateToDatabase();
    }

    public void removePersonFromMeeting(UUID meetingId, Person person) {
        // Responsible person can't be removed from the meeting
        meetings.removeIf(meeting -> meeting.getId() == meetingId
                && meeting.getResponsiblePersonId() != person.getId());
    }

    private void saveStateToDatabase() {
        try {
            objectMapper.writeValue(new File("src/main/resources/db.json"), meetings);
        } catch (IOException e) {
            log.error(String.format("Failed to save the meetings to the database. Error: %s", e.getMessage()));
        }
    }
}
