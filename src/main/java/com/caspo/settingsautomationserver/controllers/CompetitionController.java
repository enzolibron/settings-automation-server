package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.CompetitionDao;
import com.caspo.settingsautomationserver.dtos.CompetitionRequestDto;
import com.caspo.settingsautomationserver.models.Competition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionDao competitionDao;

    public CompetitionController(CompetitionDao competitionDao) {
        this.competitionDao = competitionDao;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody CompetitionRequestDto request) {
        Competition result = competitionDao.save(request.dtoToEntity());

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity getAll() {
        return new ResponseEntity(competitionDao.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        Competition result = competitionDao.get(id);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateById(@PathVariable Long id, @RequestBody CompetitionRequestDto request) {
        Competition result = competitionDao.update(request.dtoToEntity(), id);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        String result = competitionDao.delete(id);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity("Competition \"" + id + "\" does not exist.", HttpStatus.NOT_FOUND);
        }
    }

}
