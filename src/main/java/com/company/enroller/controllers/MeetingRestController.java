package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {

        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    // POST http://localhost:8080/meetings
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findByID(meeting.getId()) != null) {
            return new ResponseEntity<>("Unable to create. A meeting with id " + meeting.getId() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    // DELETE http://localhost:8080/meetings/2
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEmptyMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findByID(id);
        Collection<Participant> participants = meeting.getParticipants();
        if (participants.isEmpty()) {
            meetingService.delete(meeting);
        } else {
            return new ResponseEntity<>("Meetig with participants cannot be deleted", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    // POST http://localhost:8080/meetings/2/participants/user2
    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.POST)
    public ResponseEntity<?> assignParticipant(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meeting.getParticipants();
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>("Participant does not exist", HttpStatus.NOT_FOUND);
        }
        if (participants.contains(participant)) {
            return new ResponseEntity<>("Participant is already assigned to the meeting", HttpStatus.CONFLICT);
        }
        meetingService.addParticipant(id, login);
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    // DELETE http://localhost:8080/meetings/2/participants/user2
    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipant(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findByID(id);
        Collection<Participant> participants = meeting.getParticipants();
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>("Participant does not exist", HttpStatus.NOT_FOUND);
        }
        if (participants.contains(participant) == false) {
            return new ResponseEntity<>("Participant is not assigned to the meeting", HttpStatus.NOT_FOUND);
        }

        meetingService.removeParticipant(id, participant);
        return new ResponseEntity<Participant>(participant, HttpStatus.OK);
    }

    // GET http://localhost:8080/meetings/2/participants
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findByID(id);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting does not exist", HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meeting.getParticipants();
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);

    }

}
