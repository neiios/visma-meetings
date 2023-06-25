package com.visma.meetings.controller;

import com.visma.meetings.dto.MeetingCreationRequest;
import com.visma.meetings.model.Meeting;
import com.visma.meetings.model.Person;
import com.visma.meetings.service.MeetingService;
import org.springframework.web.bind.annotation.*;

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
    public void createMeeting(@RequestBody MeetingCreationRequest meeting) {
        meetingService.addMeeting(meeting);
    }

    @DeleteMapping("{meetingId}")
    public void deleteMeeting(@PathVariable("meetingId") UUID meetingId,
                              @RequestParam UUID requester) {
        meetingService.deleteMeeting(meetingId, requester);
    }

    @PostMapping("{meetingId}/participants")
    public void addPersonToMeeting(@PathVariable("meetingId") UUID meetingId,
                                   @RequestBody Person person) {
        meetingService.addPersonToMeeting(meetingId, person);
    }

    @DeleteMapping("{meetingId}/participants")
    public void removePersonFromMeeting(@PathVariable("meetingId") UUID meetingId,
                                        @RequestParam UUID personId) {
        meetingService.removePersonFromMeeting(meetingId, personId);
    }

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingService.getMeetings();
    }
}
