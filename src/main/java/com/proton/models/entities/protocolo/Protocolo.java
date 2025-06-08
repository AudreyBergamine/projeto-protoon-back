package com.proton.models.entities.protocolo;
// Pacote onde a entidade está localizada

// Importações necessárias para JPA, Jackson, etc.
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proton.models.entities.Comprovante;
import com.proton.models.entities.Endereco;
import com.proton.models.entities.Municipe;
import com.proton.models.entities.Secretaria;
import com.proton.models.entities.redirecionamento.Redirecionamento;
import com.proton.models.enums.Prioridade;
import com.proton.models.enums.Status;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

// Define que essa classe é uma entidade JPA que será mapeada para uma tabela no banco de dados
@Entity
@Table(name = "protocolo")
// Ignora propriedades específicas do Hibernate ao serializar para JSON
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) 
public class Protocolo implements Serializable {

	// Versão do serializable (necessário para envio de objetos em rede ou gravação)
	private static final long serialVersionUID = 1L;

	// Identificador único do protocolo (chave primária)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id_protocolo;

	// Associação muitos-para-um com a entidade Secretaria
	@ManyToOne 
	@JoinColumn(name = "id_secretariaFK", referencedColumnName = "id_secretaria")
	private Secretaria secretaria;

	// @ManyToOne //Associação Muitos para um
	// @JoinColumn(name = "id_departamentoFK",referencedColumnName =
	// "id_departamento") //nome da chave estrangeira
	// private Departamento departamento;

	// Associação muitos-para-um com a entidade Municipe
	@ManyToOne 
	@JoinColumn(name = "id_municipeFK", referencedColumnName = "id")
	private Municipe municipe; // Municipe ou empresa, por enquanto somente municipe

	// Associação muitos-para-um com a entidade Endereco
	@ManyToOne
	@JoinColumn(name = "id_enderecoFK", referencedColumnName = "id_endereco")
	private Endereco endereco;

	// Campo do assunto do protocolo
	private String assunto;

	// Número único do protocolo
	private String numero_protocolo;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =
	// "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")//Formatação da data e hora
	// Data e hora da criação do protocolo
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_protocolo; // Pega o momento da abertura do protocolo, substitui o tipo Date

	// Descrição detalhada do protocolo (campo do tipo texto longo)
	@Column(columnDefinition = "TEXT")
	private String descricao;

	// Status atual do protocolo (ex: ABERTO, CONCLUIDO, EM_ANALISE)
	private Status status;

	// Valor associado ao protocolo
	private Double valor;

	// Redirecionamentos associados ao protocolo
	@JsonIgnore
	@OneToMany(mappedBy = "protocolo", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Redirecionamento> redirecionamentos;

	// Comprovante do protocolo
    @OneToOne(mappedBy = "protocolo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Comprovante comprovante;
    
	// Prazo para conclusão do protocolo
	@Column(name = "prazo_conclusao")
	private LocalDate prazoConclusao;

	// Prioridade do protocolo (ex: ALTA, MEDIA, BAIXA)
	@Enumerated(EnumType.STRING)
	private Prioridade prioridade;

	// Construtor padrão (obrigatório para JPA)
	public Protocolo() {
	}

	// TODO: José Leandro
	
	// Construtor com parâmetros para facilitar a criação de objetos
	public Protocolo(Integer id_protocolo, Secretaria secretaria, Municipe municipe, Endereco endereco,
			String assunto, Date data_protocolo, String descricao, Status status,
			Double valor, String numero_protocolo, LocalDate prazoConclusao) {
		this.id_protocolo = id_protocolo;
		this.secretaria = secretaria;
		this.municipe = municipe;
		this.endereco = endereco;
		this.assunto = assunto;
		this.data_protocolo = data_protocolo;
		this.descricao = descricao;
		this.status = status;
		this.valor = valor;
		this.numero_protocolo = numero_protocolo;
		this.prazoConclusao = prazoConclusao;
	}

	// Getters e setters: 
	// Métodos públicos para acessar e modificar os atributos privados da classe


	public Integer getId_protocolo() {
		return id_protocolo;
	}

	public void setId_protocolo(Integer id_protocolo) {
		this.id_protocolo = id_protocolo;
	}

	public Secretaria getSecretaria() {
		return secretaria;
	}

	public void setSecretaria(Secretaria secretaria) {
		this.secretaria = secretaria;
	}

	public Municipe getMunicipe() {
		return municipe;
	}

	public void setMunicipe(Municipe municipe) {
		this.municipe = municipe;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getAssunto() {
		return assunto;
	}

	public Set<Redirecionamento> getRedirecionamentos() {
		return redirecionamentos;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public Date getData_protocolo() {
		return data_protocolo;
	}

	public void setData_protocolo(Date date) {
		this.data_protocolo = date;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getNumero_protocolo() {
		return numero_protocolo;
	}

	public void setNumero_protocolo(String numero_protocolo) {
		this.numero_protocolo = numero_protocolo;
	}

	public LocalDate getPrazoConclusao() {
		return prazoConclusao;
	}

	public void setPrazoConclusao(LocalDate prazoConclusao) {
		this.prazoConclusao = prazoConclusao;
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}

	public Comprovante getComprovante() {
		return comprovante;
	}
	
	public void setComprovante(Comprovante comprovante) {
		this.comprovante = comprovante;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id_protocolo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Protocolo other = (Protocolo) obj;
		return Objects.equals(id_protocolo, other.id_protocolo);
	}
}
