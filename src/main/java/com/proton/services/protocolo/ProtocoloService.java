//SERVICE LAYER (RESOURCER -->SERVICE LAYER(AQUI) --> REPOSITORY
package com.proton.services.protocolo;

import java.time.LocalDate;
// import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.proton.models.entities.Protocolo;
// import com.proton.models.entities.Secretaria;
import com.proton.models.entities.municipe.Municipe;
// import com.proton.models.repositories.MunicipeRepository;
//import com.proton.models.repositories.MunicipeRepository;
import com.proton.models.repositories.ProtocoloRepository;
// import com.proton.models.repositories.SecretariaRepository;
import com.proton.services.municipe.MunicipeService;

@Service
public class ProtocoloService {

	@Autowired
	private ProtocoloRepository protocoloRepository;

	@Autowired
	private MunicipeService municipeService;

	// @Autowired
	// private SecretariaRepository secretariaRepository;

	private final JdbcTemplate jdbcTemplate; // Para fazer consultas no sql

	@Autowired
	public ProtocoloService(JdbcTemplate jdbcTemplate) { // Construtor para o Spring injetar o jdbcTemplate no Protocolo
		this.jdbcTemplate = jdbcTemplate;
	}

	// Método para encontrar TODOS os protocolos
	public List<Protocolo> findAll() {
		return protocoloRepository.findAll();
	}

	// Método para encontrar protocolo pelo id
	public Protocolo findById(Integer id) {
		Optional<Protocolo> obj = protocoloRepository.findById(id);
		return obj.get();
	}

	// Método para encontrar protocolo pelo numero do protocolo
	public Protocolo findByNumero_protocolo(String numero_protocolo) {
		Optional<Protocolo> obj = protocoloRepository.findByNumeroProtocolo(numero_protocolo);
		return obj.get();
	}

	// Método para encontrar TODOS protocolos do MUNICIPE
	public List<Protocolo> findByMunicipe(Municipe municipe) {
		return protocoloRepository.findAllByMunicipe(municipe);
	}

	public List<Protocolo> findByNomeMunicipe(String nomeMunicipe) {
		Municipe municipe = municipeService.findByNome(nomeMunicipe);
		Integer idMunicipe = municipe.getId();
		return protocoloRepository.findByMunicipe(idMunicipe);
	}
	

	public void updateData(Protocolo entity, Protocolo obj) {
		entity.setSecretaria(obj.getSecretaria());
		entity.setMunicipe(obj.getMunicipe());
		entity.setEndereco(obj.getEndereco());
		entity.setAssunto(obj.getAssunto());
		entity.setDescricao(obj.getDescricao());
		entity.setValor(obj.getValor());
	}

	public Protocolo update(Integer id, Protocolo obj) {
		Protocolo entity = protocoloRepository.getReferenceById(id);
		updateData(entity, obj);
		return protocoloRepository.save(entity);
	}

	@SuppressWarnings("unused")// Serve para parar de aportar o um erro especifico ksksks, mas nem é erro.
	public String gerarNumeroProtocolo() {
		String anoAtual = String.valueOf(LocalDate.now().getYear());
		String sql = "SELECT MAX(CAST(SUBSTRING(numero_protocolo, 1, POSITION('-' IN numero_protocolo) - 1) AS UNSIGNED)) FROM Protocolo WHERE numero_protocolo LIKE ?";
		Integer ultimoNumero = jdbcTemplate.queryForObject(sql, Integer.class, "%-" + anoAtual);
	
		if (ultimoNumero == null) {
			return "001-" + anoAtual;
		} else {
			int proximoNumeroProtocolo = ultimoNumero + 1;
			String novoNumeroProtocolo = String.format("%03d", proximoNumeroProtocolo);
	
			return novoNumeroProtocolo + "-" + anoAtual;
		}
	}
	
}