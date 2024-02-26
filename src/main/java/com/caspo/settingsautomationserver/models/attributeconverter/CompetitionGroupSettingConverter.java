package com.caspo.settingsautomationserver.models.attributeconverter;

import com.caspo.settingsautomationserver.models.CompetitionGroupSetting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Converter
public class CompetitionGroupSettingConverter implements AttributeConverter<CompetitionGroupSetting, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(CompetitionGroupSetting competitionGroupSetting) {
        try {
            return objectMapper.writeValueAsString(competitionGroupSetting);
        } catch (JsonProcessingException jpe) {
            Logger.getLogger(CompetitionGroupSettingConverter.class.getName()).log(Level.SEVERE, "Cannot convert CompetitionGroupSetting into JSON", jpe);
            return null;
        }
    }

    @Override
    public CompetitionGroupSetting convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, CompetitionGroupSetting.class);
        } catch (JsonProcessingException jpe) {
            Logger.getLogger(CompetitionGroupSetting.class.getName()).log(Level.SEVERE, "Cannot convert JSON into CompetitionGroupSetting", jpe);
            return null;
        }
    }

}
