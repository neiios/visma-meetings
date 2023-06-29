package com.visma.meetings.controller;

import com.visma.meetings.dto.MeetingResponse;
import com.visma.meetings.dto.MeetingRequest;
import com.visma.meetings.dto.PersonDTO;
import com.visma.meetings.model.MeetingCategory;
import com.visma.meetings.model.MeetingType;
import com.visma.meetings.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/meetings")
public class MeetingController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public void createMeeting(@RequestBody MeetingRequest meeting) {
        meetingService.addMeeting(meeting);
    }

    @DeleteMapping("{meetingId}")
    public void deleteMeeting(@PathVariable("meetingId") UUID meetingId,
                              @RequestParam UUID requester) {
        meetingService.deleteMeeting(meetingId, requester);
    }

    @PostMapping("{meetingId}/participants")
    public ResponseEntity<String> addPersonToMeeting(@PathVariable("meetingId") UUID meetingId,
                                                     @RequestBody PersonDTO person) {
        return meetingService.addPersonToMeeting(meetingId, person);
    }

    @DeleteMapping("{meetingId}/participants")
    public void removePersonFromMeeting(@PathVariable("meetingId") UUID meetingId,
                                        @RequestParam UUID personId) {
        meetingService.removePersonFromMeeting(meetingId, personId);
    }

    @GetMapping
    public List<MeetingResponse> getMeetings(
            @RequestParam(required = false) String containsInDescription,
            @RequestParam(required = false) UUID responsiblePersonId,
            @RequestParam(required = false) MeetingCategory category,
            @RequestParam(required = false) MeetingType type,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Integer numberOfAttendees) {
        return meetingService.getMeetings(containsInDescription,
                responsiblePersonId,
                category,
                type,
                startDate,
                endDate,
                numberOfAttendees);
    }
}
