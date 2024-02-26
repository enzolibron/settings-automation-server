package com.caspo.settingsautomationserver.models.attributeconverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author 01PH1694.Lorenzo.L
 */
@Converter
public class ScheduledFutureConverter implements AttributeConverter<ScheduledFuture, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ScheduledFuture scheduledFuture) {
        try {
            return objectMapper.writeValueAsString(scheduledFuture);
        } catch (JsonProcessingException jpe) {
            Logger.getLogger(ScheduledFutureConverter.class.getName()).log(Level.SEVERE, "Cannot convert ScheduledFuture into JSON", jpe);
            return null;
        }
    }

    @Override
    public ScheduledFuture convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, ScheduledFuture.class);
        } catch (JsonProcessingException jpe) {
            Logger.getLogger(ScheduledFutureConverter.class.getName()).log(Level.SEVERE, "Cannot convert JSON into ScheduledFuture", jpe);
            return null;
        }
    }

}
