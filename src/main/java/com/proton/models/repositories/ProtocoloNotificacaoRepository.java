package com.proton.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proton.models.entities.ProtocoloNotificacao;

public interface ProtocoloNotificacaoRepository extends JpaRepository<ProtocoloNotificacao, Long> {
    List<ProtocoloNotificacao> findByEmailDestinatarioOrderByDataEnvioDesc(String email);
        // Novo método para encontrar a notificação mais recente por protocolo
        Optional<ProtocoloNotificacao> findTopByNumeroProtocoloOrderByDataEnvioDesc(String numeroProtocolo);
}