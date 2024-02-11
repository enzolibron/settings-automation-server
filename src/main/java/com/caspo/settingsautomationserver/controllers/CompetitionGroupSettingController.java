package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.CompetitionGroupSettingDao;
import com.caspo.settingsautomationserver.dtos.CompetitionGroupSettingRequestDto;
import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
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
@RequestMapping("/api/competition-group-settings")
public class CompetitionGroupSettingController {

    private final CompetitionGroupSettingDao competitionGroupSettingDao;

    public CompetitionGroupSettingController(CompetitionGroupSettingDao competitionGroupSettingDao) {
        this.competitionGroupSettingDao = competitionGroupSettingDao;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody CompetitionGroupSettingRequestDto request) {
        CompetitionGroupSetting result = competitionGroupSettingDao.save(request.dtoToEntity());

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity getAll() {

        return new ResponseEntity(competitionGroupSettingDao.getAll(), HttpStatus.OK);

    }

    @GetMapping("/{name}")
    public ResponseEntity getByName(@PathVariable String name) {
        CompetitionGroupSetting result = competitionGroupSettingDao.get(name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/{name}")
    public ResponseEntity updateByName(@PathVariable String name, @RequestBody CompetitionGroupSettingRequestDto request) {
        CompetitionGroupSetting result = competitionGroupSettingDao.update(request.dtoToEntity(), name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteByName(@PathVariable String name) {
        String result = competitionGroupSettingDao.delete(name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity("Setting \"" + name + "\" does not exist.",HttpStatus.NOT_FOUND);
        }
    }

}
