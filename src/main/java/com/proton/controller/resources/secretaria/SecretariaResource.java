package com.proton.controller.resources.secretaria;

import com.proton.models.entities.assunto.Assunto;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.entities.secretaria.Secretaria;
import com.proton.models.enums.Enum_Secretaria;
import com.proton.models.enums.Prioridade;
import com.proton.services.secretaria.SecretariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(SecretariaResource.BASE_PATH)
@RequiredArgsConstructor
public class SecretariaResource {

    public static final String BASE_PATH = "protoon/secretaria";

    private final SecretariaService secretariaService;

    @PostMapping("/{idEnd}")
    public ResponseEntity<Secretaria> insert(@RequestBody Secretaria secretaria, @PathVariable Integer idEnd) {
        Secretaria entity = secretariaService.insert(secretaria, idEnd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId_secretaria())
                .toUri();
        return ResponseEntity.created(location).body(entity);
    }

    @GetMapping
    public ResponseEntity<List<Secretaria>> findAll() {
        return ResponseEntity.ok(secretariaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Secretaria> findById(@PathVariable Long id) {
        return ResponseEntity.ok(secretariaService.findById(id));
    }

    @GetMapping("/listar-secretarias")
    public List<Map<String, Object>> listarSecretarias() {
        List<Map<String, Object>> secretarias = new ArrayList<>();

        for (Enum_Secretaria secretaria : Enum_Secretaria.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id_secretaria", secretaria.getId());
            item.put("nome_secretaria", secretaria.toString());
            secretarias.add(item);
        }

        return secretarias;
    }

    @GetMapping("/protocolos/{id}")
    public ResponseEntity<List<Protocolo>> findProtocolosBySecretariaId(@PathVariable Long id) {
        Secretaria sec = secretariaService.findById(id);

        if (sec == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sec.getProtocolos());
    }
}
