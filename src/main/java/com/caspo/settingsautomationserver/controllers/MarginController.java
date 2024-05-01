package com.caspo.settingsautomationserver.controllers;

import com.caspo.settingsautomationserver.daos.MarginDao;
import com.caspo.settingsautomationserver.dtos.MarginDto;
import com.caspo.settingsautomationserver.models.Margin;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/api/margins")
public class MarginController {

    private final MarginDao marginDao;

    @PostMapping
    public ResponseEntity createByBatch(@RequestBody List<MarginDto> request) {
        List<Margin> requestEntityList = request.stream()
                .map(item -> item.dtoToEntity()).collect(Collectors.toList());

        List<Margin> result = marginDao.saveAll(requestEntityList);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAll() {
        return new ResponseEntity(marginDao.getAllMargins(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity getByGroupName(@PathVariable String name) {
        return new ResponseEntity(marginDao.getMarginsByGroupName(name), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity getByProp(@RequestBody MarginDto request) {
        return new ResponseEntity(marginDao.getAllMarginsByProp(request.getMarginGroupName(), request.getSportId(), request.getBetTypeName(), request.getBetTypeId(), request.getMarketTypeId(), request.getIsRbMarket()), HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity deleteMarginsByGroupName(@PathVariable String name) {
        String result = marginDao.deleteByGroupName(name);

        if (result != null) {
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            return new ResponseEntity("group name \"" + name + "\" does not exist.", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity updateByBatch(@RequestBody List<MarginDto> request) {
        List<Margin> requestEntityList = request.stream()
                .map(item -> item.dtoToEntity()).collect(Collectors.toList());

        List<Margin> result = marginDao.update(requestEntityList);

        return new ResponseEntity(result, HttpStatus.OK);
    }

}
