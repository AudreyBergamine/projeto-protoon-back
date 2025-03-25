//SERVICE LAYER (RESOURCER -->SERVICE LAYER(AQUI) --> REPOSITORY

package com.proton.services.Assunto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proton.models.entities.assunto.Assunto;
import com.proton.models.entities.secretaria.Secretaria;
import com.proton.models.repositories.AssuntoRepository;
import com.proton.models.repositories.SecretariaRepository;
import com.proton.models.enums.Prioridade;

@Service
public class AssuntoService {

    @Autowired
    private AssuntoRepository assuntoRepository;

    @Autowired
    private SecretariaRepository secretariaRepository;

    public List<Assunto> findAll() {
        return assuntoRepository.findAll();
    }

    public Assunto findById(Integer id) {
        Optional<Assunto> obj = assuntoRepository.findById(id);
        return obj.orElseThrow(() -> new RuntimeException("Assunto não encontrado"));
    }

    public void updateData(Assunto entity, Assunto obj) {
        entity.setAssunto(obj.getAssunto());
        entity.setSecretaria(obj.getSecretaria());
        entity.setPrioridade(obj.getPrioridade()); // Atualiza prioridade
    }

    public Assunto update(Integer id, Assunto obj) {
        Assunto entity = assuntoRepository.getReferenceById(id);
        updateData(entity, obj);
        return assuntoRepository.save(entity);
    }

    public Assunto create(Assunto obj) {
        Secretaria secretaria = secretariaRepository.findById(obj.getSecretaria().getId_secretaria())
                .orElseThrow(() -> new RuntimeException("Secretaria não encontrada"));
        obj.setSecretaria(secretaria);
        if (obj.getPrioridade() == null) {
            throw new IllegalArgumentException("Prioridade não informada");
        }
        // Busca a prioridade pelo ID, achei mais facil assim
        Prioridade prioridade = Prioridade.fromId(obj.getPrioridade().getId()); // Acessando o ID da prioridade
        obj.setPrioridade(prioridade); // Define a prioridade no objeto
        // Define o tempo de resolução, usando o valor do enum
        obj.setTempoResolucao(prioridade.getDiasParaResolver());
        return assuntoRepository.save(obj);
    }

    public Prioridade determinarPrioridade(String nomeAssunto) {
        Assunto assunto = assuntoRepository.findByAssunto(nomeAssunto);
        
        if (assunto != null && assunto.getPrioridade() != null) {
            return assunto.getPrioridade(); // Retorna a prioridade cadastrada no banco
        }
        
        return Prioridade.MEDIA; // Define um padrão caso não encontre
    }
    
}
