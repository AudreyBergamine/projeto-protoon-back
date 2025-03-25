package com.proton.models.enums;

public enum Enum_Secretaria {
    ADMINISTRACAO(1),
    AGRICULTURA(2),
    ASSISTENCIA_SOCIAL(3),
    ASSUNTOS_JURIDICOS(4),
    COMUNICACAO(5),
    CULTURA(6),
    DEFESA_CIVIL(7),
    DESENVOLVIMENTO_ECONOMICO(8),
    DESENVOLVIMENTO_HABITACIONAL(9),
    EDUCACAO(10),
    ESPORTE_E_LAZER(11),
    FAVELAS(12),
    FAZENDA(13),
    GOVERNO(14),
    MEIO_AMBIENTE(15),
    RELACOES_INSTITUCIONAIS(16),
    OBRAS(17),
    PLANEJAMENTO_URBANO(18),
    PROTECAO_ANIMAL(19),
    RECURSOS_HUMANOS(20),
    SAUDE(21),
    SERVICOS_URBANOS(22),
    SEGURANCA_URBANA(23),
    TRANSPORTE_E_MOBILIDADE_URBANA(24),
    TURISMO(25);

    private final int id;

    Enum_Secretaria(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static Enum_Secretaria getById(int id) {
        for (Enum_Secretaria s : Enum_Secretaria.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("ID inválido: " + id);
    }

    @Override
    public String toString() {
        switch (this) {
            case ADMINISTRACAO:
                return "Secretaria de Administração";
            case AGRICULTURA:
                return "Secretaria de Agricultura";
            case ASSISTENCIA_SOCIAL:
                return "Secretaria de Assistência Social";
            case ASSUNTOS_JURIDICOS:
                return "Secretaria de Assuntos Jurídicos";
            case COMUNICACAO:
                return "Secretaria de Comunicação";
            case CULTURA:
                return "Secretaria Cultura";
            case DEFESA_CIVIL:
                return "Secretaria de Defesa Civil";
            case DESENVOLVIMENTO_ECONOMICO:
                return "Secretaria de Desenvolvimento Econômico";
            case DESENVOLVIMENTO_HABITACIONAL:
                return "Secretaria de Desenvolvimento Habitacional";
            case EDUCACAO:
                return "Secretaria de Educação";
            case ESPORTE_E_LAZER:
                return "Secretaria de Esporte e Lazer";
            case FAVELAS:
                return "Secretaria de Favelas";
            case FAZENDA:
                return "Secretaria de Fazenda";
            case GOVERNO:
                return "Secretaria de Governo";
            case MEIO_AMBIENTE:
                return "Secretaria de Meio Ambiente";
            case RELACOES_INSTITUCIONAIS:
                return "Secretaria de Relações Institucionais";
            case OBRAS:
                return "Secretaria de Obras";
            case PLANEJAMENTO_URBANO:
                return "Secretaria de Planejamento Urbano";
            case PROTECAO_ANIMAL:
                return "Secretaria de Proteção Animal";
            case RECURSOS_HUMANOS:
                return "Secretaria de Recursos Humanos";
            case SAUDE:
                return "Secretaria de Saúde";
            case SERVICOS_URBANOS:
                return "Secretaria de Serviços Urbanos";
            case SEGURANCA_URBANA:
                return "Secretaria de Segurança Urbana";
            case TRANSPORTE_E_MOBILIDADE_URBANA:
                return "Secretaria de Transporte e Mobilidade Urbana";
            case TURISMO:
                return "Secretaria de Turismo";
            default:
                return super.toString();
        }
    }
}