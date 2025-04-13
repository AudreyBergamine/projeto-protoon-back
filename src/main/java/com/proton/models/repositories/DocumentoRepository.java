package com.proton.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proton.models.entities.protocolo.Documento;
import com.proton.models.entities.protocolo.Protocolo;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
    Optional<Documento> findById(Long id);

    List<Documento> findByProtocolo(Protocolo protocolo);

    @Query("SELECT d FROM Documento d WHERE d.protocolo.id_protocolo = :protocoloId")
    List<Documento> findByProtocoloId(@Param("protocoloId") Integer protocoloId);

    void deleteByProtocolo(Protocolo protocolo);
}
