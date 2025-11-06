// ...existing code...
package com.restaurante.demo;

public enum StatusMesaEnum {
    DISPONIVEL("disponivel"),
    RESERVADA("reservada"),
    INATIVA("inativa");

    private final String dbValue;

    StatusMesaEnum(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static StatusMesaEnum fromDbValue(String value) {
        if (value == null) return DISPONIVEL;
        for (StatusMesaEnum s : values()) {
            if (s.dbValue.equalsIgnoreCase(value)) return s;
        }
        return DISPONIVEL;
    }

    @Override
    public String toString() {
        return dbValue;
    }
}