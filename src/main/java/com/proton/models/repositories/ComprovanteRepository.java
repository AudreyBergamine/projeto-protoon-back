package com.proton.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proton.models.entities.comprovante.Comprovante;
import com.proton.models.entities.protocolo.Protocolo;

public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
      Optional<Comprovante> findByProtocolo(Protocolo protocolo);

}