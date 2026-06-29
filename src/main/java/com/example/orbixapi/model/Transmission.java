package com.example.orbixapi.model;

public enum Transmission {
    AUTOMATICO("Automático"),
    MANUAL("Manual");

    private final String label;

    Transmission(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
