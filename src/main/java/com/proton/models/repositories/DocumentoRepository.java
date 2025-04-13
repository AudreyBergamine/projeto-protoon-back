package com.proton.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proton.models.entities.protocolo.Documento;
import com.proton.models.entities.protocolo.Protocolo;


public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    Optional<Documento> findById(Long id);
    List<Documento> findByProtocolo(Protocolo protocolo);
    List<Documento> findByProtocoloId(Integer protocoloId);
    void deleteByProtocolo(Protocolo protocolo);
}
