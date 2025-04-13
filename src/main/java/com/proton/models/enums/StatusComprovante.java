package com.proton.models.enums;

public enum StatusComprovante {
    PENDENTE,
    APROVADO,
    REPROVADO;
    
    // Se necessário, adicione este método para facilitar a conversão
    public static StatusComprovante fromString(String value) {
        if (value == null) return null;
        try {
            return StatusComprovante.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}