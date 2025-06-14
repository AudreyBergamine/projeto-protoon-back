package com.proton.models.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.Prioridade;
import com.proton.models.enums.StatusComprovante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO VAGNER MODEL COMPROVANTE

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "comprovante")
public class Comprovante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo; //nome do arquivo escolhido

    @Column(nullable = false)
    private String tipoArquivo; //jpg, pdf, png etc.

    @Column(nullable = false)
    private String caminhoArquivo; // Ou pode usar @Lob para armazenar o arquivo diretamente no banco

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUpload;

    private Long tamanhoArquivo;

    private String urlDownload; // URL para download via localhost

    @Enumerated(EnumType.STRING)
    private StatusComprovante status = StatusComprovante.PENDENTE; // Valor padrão

    @ManyToOne(fetch = FetchType.LAZY) //1xn 1= protocolo e n = comprovante
    @JsonIgnore
    @JoinColumn(name = "protocolo_id", nullable = false)
    private Protocolo protocolo;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    public Prioridade getPrioridade() {
        return this.prioridade;
    }

}
