package com.proton.models.enums;

public enum Prioridade {
    BAIXA(1, 7),   // ID: 1, Dias para resolver: 7
    MEDIA(2, 5),   // ID: 2, Dias para resolver: 5
    ALTA(3, 3),    // ID: 3, Dias para resolver: 3
    URGENTE(4, 1); // ID: 4, Dias para resolver: 1

    private final int id; // ID numérico
    private final int diasParaResolver; // Dias para resolver o assunto

    // Construtor do enum
    Prioridade(int id, int diasParaResolver) {
        this.id = id;
        this.diasParaResolver = diasParaResolver;
    }

    // Métodos para acessar os campos
    public int getId() {
        return id;
    }

    public int getDiasParaResolver() {
        return diasParaResolver;
    }

    // Método para obter o enum pelo ID 
    public static Prioridade fromId(int id) {
        for (Prioridade p : Prioridade.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }
}
