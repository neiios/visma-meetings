package com.visma.meetings.repository;

import com.visma.meetings.exception.ResourceNotFoundException;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.MeetingCategory;
import com.visma.meetings.model.MeetingType;
import com.visma.meetings.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @BeforeEach
    void setUp() {
        meetingRepository.dropAll();
    }

    @Test
    void getMeetings() {
        Person responsiblePerson = new Person(UUID.randomUUID(), "Jack");
        Meeting testMeeting = new Meeting(UUID.randomUUID(),
                "Meeting name",
                responsiblePerson.getId(),
                "Meeting description",
                MeetingCategory.CODE_MONKEY,
                MeetingType.IN_PERSON,
                LocalDateTime.of(2023, 6, 30, 10, 15),
                LocalDateTime.of(2023, 6, 30, 10, 15),
                List.of(responsiblePerson));
        meetingRepository.addMeeting(testMeeting);

        assertEquals(List.of(testMeeting), meetingRepository.getMeetings());
    }

    @Test
    void deleteMeeting() {
        Person responsiblePerson = new Person(UUID.randomUUID(), "Jack");
        UUID newMeetingUUID = UUID.randomUUID();
        Meeting testMeeting = new Meeting(newMeetingUUID,
                "Meeting name",
                responsiblePerson.getId(),
                "Meeting description",
                MeetingCategory.CODE_MONKEY,
                MeetingType.IN_PERSON,
                LocalDateTime.of(2023, 6, 30, 10, 15),
                LocalDateTime.of(2023, 6, 30, 10, 15),
                List.of(responsiblePerson));
        meetingRepository.addMeeting(testMeeting);

        meetingRepository.deleteMeeting(newMeetingUUID);
        assertEquals(List.of(), meetingRepository.getMeetings());
    }

    @Test
    void removePersonFromMeeting() {
        Person responsiblePerson = new Person(UUID.randomUUID(), "Jack");
        Person anotherPerson = new Person(UUID.randomUUID(), "John");
        UUID newMeetingUUID = UUID.randomUUID();
        Meeting testMeeting = new Meeting(newMeetingUUID,
                "Meeting name",
                responsiblePerson.getId(),
                "Meeting description",
                MeetingCategory.CODE_MONKEY,
                MeetingType.IN_PERSON,
                LocalDateTime.of(2023, 6, 30, 10, 15),
                LocalDateTime.of(2023, 6, 30, 10, 15),
                List.of(responsiblePerson, anotherPerson));
        meetingRepository.addMeeting(testMeeting);

        meetingRepository.removePersonFromMeeting(newMeetingUUID, anotherPerson.getId());
        List<Person> returnedParticipants = meetingRepository.getMeetings().get(0).getParticipants();

        assertNotEquals(List.of(responsiblePerson, anotherPerson), returnedParticipants);
    }

    @Test
    void removePersonFromMeetingDoesNotExist() {
        Person responsiblePerson = new Person(UUID.randomUUID(), "Jack");
        Person anotherPerson = new Person(UUID.randomUUID(), "John");
        UUID zeroUUID = new UUID(0,0);
        Meeting testMeeting = new Meeting(UUID.randomUUID(),
                "Meeting name",
                responsiblePerson.getId(),
                "Meeting description",
                MeetingCategory.CODE_MONKEY,
                MeetingType.IN_PERSON,
                LocalDateTime.of(2023, 6, 30, 10, 15),
                LocalDateTime.of(2023, 6, 30, 10, 15),
                List.of(responsiblePerson, anotherPerson));
        meetingRepository.addMeeting(testMeeting);

        UUID anotherPersonUUID = anotherPerson.getId();
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                meetingRepository.removePersonFromMeeting(zeroUUID, anotherPersonUUID));

        assertTrue(exception.getMessage().contains("does not exist"));
    }
}
