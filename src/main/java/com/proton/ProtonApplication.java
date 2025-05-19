package com.proton;

// TODO:     *** ARQUIVO PARA RODAR A APLICAÇÃO ***

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // ESSA ANOTAÇÃO É OBRIGATÓRIA
public class ProtonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtonApplication.class, args);
		System.out.println("\u001B[32m \nAplicação iniciada com SUCESSO!!! \u001B[0m");
		//Run();
	}
}
