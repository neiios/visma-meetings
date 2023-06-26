package com.visma.meetings.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.visma.meetings.exception.ResourceNotFoundException;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Repository
@Slf4j
public class MeetingRepository {
    private final List<Meeting> meetings;
    private final ObjectMapper objectMapper;
    private final String pathToDB;

    public MeetingRepository(ObjectMapper objectMapper, Environment env) {
        List<Meeting> tempMeetings = new ArrayList<>();
        this.objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.pathToDB = Optional.ofNullable(env.getProperty("custom.file_path"))
                .orElse("src/test/resources/db.json");

        try {
            tempMeetings = objectMapper.readValue(
                    new File(pathToDB),
                    new TypeReference<List<Meeting>>() {
                    });
            log.info(String.format("Read the data from %s", pathToDB));
        } catch (IOException e) {
            log.error(String.format("Failed to read saved meetings. Error: %s", e.getMessage()));
        }

        meetings = tempMeetings;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void deleteMeeting(UUID id) {
        meetings.removeIf(meeting -> meeting.getId().equals(id));
        saveStateToDatabase();
    }

    public void addMeeting(Meeting meeting) {
        meetings.add(meeting);
        saveStateToDatabase();
    }

    public void removePersonFromMeeting(UUID meetingId, UUID personId) {
        Meeting requestedMeeting = meetings.stream()
                .filter(meeting -> meeting.getId().equals(meetingId))
                .findAny()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting with ID [%s] does not exist.".formatted(meetingId)));

        List<Person> newParticipants = requestedMeeting.getParticipants().stream()
                .filter(Predicate.not(participant -> participant.getId().equals(personId)))
                .toList();

        requestedMeeting.setParticipants(newParticipants);
        saveStateToDatabase();
    }

    public void dropAll() {
        meetings.clear();
        saveStateToDatabase();
    }

    private void saveStateToDatabase() {
        try {
            objectMapper.writeValue(new File(pathToDB), meetings);
        } catch (IOException e) {
            log.error(String.format("Failed to save the meetings to the database. Error: %s", e.getMessage()));
        }
    }
}
