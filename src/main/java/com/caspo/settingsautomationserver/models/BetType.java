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
@Entity(name = "bet_type")
public class BetType {

    @Id
    private Long betTypeId;

    @Column(name = "market_type_id")
    private String marketTypeId;

}
