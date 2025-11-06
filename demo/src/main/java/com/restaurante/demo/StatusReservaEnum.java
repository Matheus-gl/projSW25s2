package com.restaurante.demo;

public enum StatusReservaEnum {
    PENDENTE("pendente"),
    CONFIRMADA("confirmada"),
    CANCELADA("cancelada"),
    CONCLUIDA("concluida");

    private final String db;

    StatusReservaEnum(String db) { this.db = db; }

    public String getDb() { return db; }

    public static StatusReservaEnum fromDb(String value) {
        if (value == null) return PENDENTE;
        for (StatusReservaEnum s : values()) {
            if (s.db.equalsIgnoreCase(value)) return s;
        }
        return PENDENTE;
    }

    @Override
    public String toString() { return db; }
}