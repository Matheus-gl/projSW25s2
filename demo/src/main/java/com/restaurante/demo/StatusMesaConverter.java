// ...existing code...
package com.restaurante.demo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class StatusMesaConverter implements AttributeConverter<StatusMesaEnum, String> {

    @Override
    public String convertToDatabaseColumn(StatusMesaEnum attribute) {
        return (attribute == null) ? StatusMesaEnum.DISPONIVEL.getDbValue() : attribute.getDbValue();
    }

    @Override
    public StatusMesaEnum convertToEntityAttribute(String dbData) {
        return StatusMesaEnum.fromDbValue(dbData);
    }
}