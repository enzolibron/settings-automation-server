package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.dtos.EventDto;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.services.EventService;
import com.caspo.settingsautomationserver.services.EventSettingService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventDao eventDao;
    private final EventService eventService;
    private final EventSettingService eventSettingService;

    @GetMapping
    public ResponseEntity getAll() {
        List<EventDto> result = eventDao.getAll().stream()
                .map(event -> EventDto.buildDto(event)).collect(Collectors.toList());

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody EventDto request) {
        Event result = eventService.updateEvent(request.dtoToEntity());

        if (result == null) {
            return new ResponseEntity("Event doesn't exist", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(EventDto.buildDto(result), HttpStatus.OK);
        }

    }

    @PostMapping("/process-child/{eventId}")
    public ResponseEntity processChildEvents(@PathVariable String eventId) {
        Event event = eventDao.get(eventId);

        if (event != null) {
            eventSettingService.processChildEvents(event, false);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity("event " + eventId + " doesn't exist", HttpStatus.BAD_REQUEST);
        }
    }

}
