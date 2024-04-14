package com.proton.models.entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "departamento")
@Data
public class Departamento implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_departamento;
	
	private String nome_departamento;
	private String nome_responsavel;
	private String email;
	private String senha;
	

	@OneToOne
	@JoinColumn(name = "id_enderecoFK")
	private Endereco endereco;
    
	private Departamento(){
		
	}

	public Departamento(Long id_departamento, String nome_departamento, String nome_responsavel, String email, String senha, 
			Endereco id_enderecoFK ) {
		super();
		this.id_departamento = id_departamento;
		this.nome_departamento = nome_departamento;
		this.nome_responsavel = nome_responsavel;
		this.email = email;
		this.senha = senha;	
		this.endereco = id_enderecoFK;
	}
}
