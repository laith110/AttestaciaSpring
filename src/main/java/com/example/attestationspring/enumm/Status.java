package com.example.attestationspring.enumm;
public enum Status {
    Принят("Принят"),
    Оформлен("Оформлен"),
    Ожидает("Ожидает"),
    Получен("Получен");




    private final String displayValue;
    Status(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
