package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.EventDao;
import com.caspo.settingsautomationserver.dtos.EventDto;
import com.caspo.settingsautomationserver.models.Event;
import com.caspo.settingsautomationserver.services.EventService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

}
