package com.restaurante.demo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class StatusReservaConverter implements AttributeConverter<StatusReservaEnum, String> {

    @Override
    public String convertToDatabaseColumn(StatusReservaEnum attribute) {
        return (attribute == null) ? StatusReservaEnum.PENDENTE.getDb() : attribute.getDb();
    }

    @Override
    public StatusReservaEnum convertToEntityAttribute(String dbData) {
        return StatusReservaEnum.fromDb(dbData);
    }
}