package com.proton.models.entities;

import java.io.Serializable;

import com.proton.models.enums.Prioridade;

import jakarta.persistence.*;

@Entity
@Table(name = "assunto")
public class Assunto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_assunto;
    private String assunto;
    private Double valor_protocolo;

    @ManyToOne
    @JoinColumn(name = "id_secretaria")
    private Secretaria secretaria;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;
    
    private Integer tempoResolucao; // Tempo em dias

    public Assunto() {
    }

    public Assunto(Integer id_assunto, String assunto, Secretaria secretaria, Double valor_protocolo, Prioridade prioridade, Integer tempoResolucao) {
        this.id_assunto = id_assunto;
        this.assunto = assunto;
        this.secretaria = secretaria;
        this.valor_protocolo = valor_protocolo;
        this.prioridade = prioridade;
        this.tempoResolucao = tempoResolucao;
    }

    public Integer getId_assunto() {
        return id_assunto;
    }

    public void setId_assunto(Integer id_assunto) {
        this.id_assunto = id_assunto;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

    public Double getValor_protocolo() {
        return valor_protocolo;
    }

    public void setValor_protocolo(Double valor_protocolo) {
        this.valor_protocolo = valor_protocolo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Integer getTempoResolucao() {
        return tempoResolucao;
    }

    public void setTempoResolucao(Integer tempoResolucao) {
        this.tempoResolucao = tempoResolucao;
    }
}
