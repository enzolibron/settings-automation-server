package com.caspo.settingsautomationserver.dtos;

import com.caspo.settingsautomationserver.models.Margin;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Data
public class MarginDto implements Dto<Margin> {
    
    private Long id;
    private String marginGroupName;
    private Integer sportId;
    private String betTypeName;
    private Integer betTypeId;
    private Integer marketTypeId;
    private Double margin;

    @Override
    public Margin dtoToEntity() {
        Margin entity = new Margin();
        entity.setId(this.id);
        entity.setBetTypeId(this.betTypeId);
        entity.setBetTypeName(this.betTypeName);
        entity.setMargin(this.margin);
        entity.setMarginGroupName(this.marginGroupName);
        entity.setMarketTypeId(this.marketTypeId);
        entity.setSportId(this.sportId);
        
        return entity;
    }

}
