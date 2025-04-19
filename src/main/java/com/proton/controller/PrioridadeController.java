package com.proton.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proton.models.enums.Prioridade;

@RestController
@RequestMapping(value = "/protoon/prioridades")
public class PrioridadeController {

    @GetMapping
    public ResponseEntity<List<Prioridade>> findAll() {
        // Retorna todos os valores do enum como uma lista
        List<Prioridade> list = Arrays.asList(Prioridade.values());
        return ResponseEntity.ok().body(list);
    }
}
