package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.ParentChildSettingDao;
import com.caspo.settingsautomationserver.dtos.ParentChildSettingDto;
import com.caspo.settingsautomationserver.models.ParentChildSetting;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/parent-child-settings")
public class ParentChildSettingController {

    private final ParentChildSettingDao parentChildSettingDao;

    @PostMapping
    public ResponseEntity create(@RequestBody ParentChildSettingDto request) {
        ParentChildSetting result = parentChildSettingDao.save(request.dtoToEntity());

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity getAll() {
        return new ResponseEntity(parentChildSettingDao.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity getByName(@PathVariable String name) {
        ParentChildSetting result = parentChildSettingDao.get(name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{name}")
    public ResponseEntity updateByName(@PathVariable String name, @RequestBody ParentChildSettingDto request) {
        ParentChildSetting result = parentChildSettingDao.update(request.dtoToEntity(), name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteByName(@PathVariable String name) {
        String result = parentChildSettingDao.delete(name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity(name + " does not exist.", HttpStatus.NOT_FOUND);
        }
    }

}
