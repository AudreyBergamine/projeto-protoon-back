package com.proton.services;

import com.proton.models.entities.Endereco;
import com.proton.models.entities.Log;
import com.proton.models.entities.Secretaria;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.repositories.EnderecoRepository;
import com.proton.models.repositories.LogRepository;
import com.proton.models.repositories.SecretariaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecretariaService {

	private final SecretariaRepository repository; // Repositório de dados para acesso a secretaria
	private final EnderecoRepository enderecoRepository; // Repositório de dados para acesso ao endereco
	private final LogRepository logRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Transactional
	public Secretaria insert(Secretaria secretaria, Integer idEnd) {
		Endereco endereco = enderecoRepository.findById(idEnd)
				.orElseThrow(() -> new RuntimeException("Endereço não encontrado")); // Tratamento de erro se o Endereço
																						// não for encontrado
		secretaria.setEndereco(endereco);

		String mensagemLog = String.format(
				"%s cadastrou uma nova Secretaria: " + secretaria.getNome_secretaria() + " em %s",
				secretaria.getNomeResponsavel(), LocalDateTime.now().format(formatter));

		Log log = new Log();
		log.setMensagem(mensagemLog);
		logRepository.save(log);

		return repository.save(secretaria); // Salva a secretaria associada ao endereço
	}

	public List<Secretaria> findAll() {
		return repository.findAll(); // Retorna todas as secretarias
	}

	public Secretaria findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Secretaria não encontrada com id: " + id)); // Retorna a
																										// secretaria ou
																										// lança erro
	}

	public List<Protocolo> findProtocolosBySecretariaId(Long id) {
		Secretaria secretaria = findById(id); // Reutiliza o método findById para obter a secretaria
		return secretaria.getProtocolos(); // Retorna os protocolos associados à secretaria
	}
}
