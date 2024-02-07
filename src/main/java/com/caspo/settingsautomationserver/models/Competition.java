package com.caspo.settingsautomationserver.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@Entity(name = "competitions")
public class Competition {
    
    @Id
    private Long id;
    
    @Column(name = "competition")
    private String competition;
    
    @Column(name = "settings")
    private String settings;
}
