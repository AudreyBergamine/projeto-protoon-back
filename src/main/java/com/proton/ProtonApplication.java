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
		System.out.println("\nRodando...\n");
		//Run();
	}
}
