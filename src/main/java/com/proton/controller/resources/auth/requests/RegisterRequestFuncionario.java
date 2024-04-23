package com.proton.controller.resources.auth.requests;

import java.time.LocalDate;

//import com.proton.models.entities.Departamento;
import com.proton.models.entities.Endereco;
import com.proton.models.entities.Secretaria;
import com.proton.models.entities.roles.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestFuncionario {

  private String nome;
  private String email;
  private String senha;
  private String num_CPF;
  private String celular;
  private Role role;
  private String numTelefoneFixo;
  private LocalDate data_nascimento;
  private Endereco endereco;
  private Secretaria secretaria;
  //private Departamento departamento;

  
}
