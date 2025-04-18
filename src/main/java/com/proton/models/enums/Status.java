package com.proton.models.enums;

public enum Status {
    PAGAMENTO_PENDENTE(1, "Pagamento Pendente"),
    EM_ANDAMENTO(2, "Em Andamento"),
    CIENCIA(3, "Ciência"), //TODO: Poderia ser "Em Análise? "
    CIENCIA_ENTREGA(4, "Ciência e entrega"), // TODO: Poderia ser "Em Análise e Entrega?"
    CONCLUIDO(5, "Concluído"),
    CANCELADO(6, "Cancelado"),
    RECUSADO(7, "Recusado");

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
