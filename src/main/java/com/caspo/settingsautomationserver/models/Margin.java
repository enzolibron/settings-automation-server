package com.caspo.settingsautomationserver.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
@Entity(name = "margins")
public class Margin {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "margin_group_name")
    private String marginGroupName;

    @Column(name = "sport_id")
    private Integer sportId;

    @Column(name = "bet_type_name")
    private String betTypeName;

    @Column(name = "bet_type_id")
    private Integer betTypeId;

    @Column(name = "market_type_id")
    private Integer marketTypeId;

    @Column(name = "margin")
    private Double margin;

}
