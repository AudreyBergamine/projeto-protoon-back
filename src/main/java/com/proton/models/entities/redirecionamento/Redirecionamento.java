package com.proton.models.entities.redirecionamento;

import java.time.LocalDateTime;

import com.proton.models.entities.BaseEntity;
import com.proton.models.entities.Funcionario;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.StatusRedirecionamento;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Redirecionamento extends BaseEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    private StatusRedirecionamento statusRedirecionamento;
    private String descricao;
    private String novaSecretaria;
    private LocalDateTime dtSolicitacao; //Not Null
    private LocalDateTime dtConfirmacao; //NullAble

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "protocolo_id", nullable = false)
    private Protocolo protocolo;


    public Redirecionamento(Funcionario fun, Protocolo prot, StatusRedirecionamento statusRedirecionamento, String descricao, LocalDateTime dtSolicitacao, LocalDateTime dtConfirmacao) {
        super();
        this.statusRedirecionamento = statusRedirecionamento;
        this.descricao = descricao;
        this.dtSolicitacao = dtSolicitacao;
        this.dtConfirmacao = dtConfirmacao;
        this.funcionario = fun;
        this.protocolo = prot;
    }
    



    
    

}
