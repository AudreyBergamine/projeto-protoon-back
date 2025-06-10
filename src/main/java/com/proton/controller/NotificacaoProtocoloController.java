package com.proton.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proton.services.NotificacaoProtocoloService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/protocolos")
public class NotificacaoProtocoloController {

    @Autowired
    private final NotificacaoProtocoloService notificacaoService;

    public NotificacaoProtocoloController(NotificacaoProtocoloService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }


    @PostMapping("/{numeroProtocolo}/notificar")
    public ResponseEntity<String> notificarAtualizacao(
            @PathVariable String numeroProtocolo,
            @RequestParam String email,
            @RequestBody String mensagemAtualizacao) {
        
        String resultado = notificacaoService.enviarNotificacaoProtocolo(
            email, 
            numeroProtocolo, 
            mensagemAtualizacao);
        
        return ResponseEntity.ok(resultado);
    }
}