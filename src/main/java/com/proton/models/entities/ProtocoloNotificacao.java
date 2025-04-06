package com.proton.models.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class ProtocoloNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String emailDestinatario;
    private String numeroProtocolo;
    @Column(length = 2000) // ou use @Lob para texto muito longo
    private String mensagem;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;
    
    private String status; // PENDENTE, ENVIADO, FALHA
    
    // Getters e Setters
}
