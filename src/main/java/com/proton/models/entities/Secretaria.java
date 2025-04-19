package com.proton.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.proton.models.entities.protocolo.Protocolo;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "secretaria")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "funcionarios", "protocolos" }) // Evita loops recursivos na serialização
public class Secretaria extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id_secretaria;

	@JsonProperty("nome_secretaria")
	private String nome_secretaria;

	@JsonProperty("nome_responsavel")
	private String nomeResponsavel;

	private String email;

	@ManyToOne
	@JoinColumn(name = "id_enderecoFK", referencedColumnName = "id_endereco")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Endereco endereco;

	@JsonIgnore
	@OneToMany(mappedBy = "secretaria")
	private final List<Funcionario> funcionarios = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "secretaria")
	private final List<Protocolo> protocolos = new ArrayList<>();

	public Long getId_secretaria() {
		return id_secretaria;
	}
}
