package com.proton.models.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proton.models.entities.Comprovante;
import com.proton.models.entities.protocolo.Protocolo;

public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
      Optional<Comprovante> findByProtocolo(Protocolo protocolo);

}