package com.proton.models.enums;

public enum Status {
    PAGAMENTO_PENDENTE(1, "Pagamento Pendente"),
    EM_ANALISE(2, "Em Análise"),
    EM_ANDAMENTO(3, "Em Andamento"),
    CIENCIA(4, "Ciência"), 
    CIENCIA_ENTREGA(5, "Ciência e entrega"),
    CONCLUIDO(6, "Concluído"),
    CANCELADO(7, "Cancelado"),
    RECUSADO(8, "Recusado");

    private final int id; //private final, para não ser alterado
    private final String descricao; //private final, para não ser alterado

    Status(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    // Método para obter o enum pelo ID
    public static Status getById(int id) {
        for (Status status : Status.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("ID de status inválido: " + id);
    }
}
