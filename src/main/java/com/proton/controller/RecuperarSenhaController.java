package com.proton.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proton.services.RecuperarSenhaService;

@RestController
@RequestMapping("/protoon/municipe/municipes/recuperarSenha")
public class RecuperarSenhaController {

    @Autowired
    private RecuperarSenhaService recuperarSenhaService;

    @PostMapping("/solicitar-codigo")
    public ResponseEntity<?> solicitarCodigo(@RequestBody SolicitarCodigoRequest request) {
        String resultado = recuperarSenhaService.enviarCodigoEmail(request.email());
        return ResponseEntity.ok().body(new RespostaGenerica(resultado));
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<?> validarCodigo(@RequestBody ValidarCodigoRequest request) {
        boolean valido = recuperarSenhaService.validarCodigo(request.email(), request.codigo());
        if (valido) {
            return ResponseEntity.ok().body(new RespostaGenerica("Código válido"));
        }
        return ResponseEntity.badRequest().body(new RespostaGenerica("Código inválido ou expirado"));
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaRequest request) {
        String resultado = recuperarSenhaService.alterarSenha(
            request.email(), 
            request.codigo(), 
            request.novaSenha()
        );
        if (resultado.startsWith("Erro")) {
            return ResponseEntity.badRequest().body(new RespostaGenerica(resultado));
        }
        return ResponseEntity.ok().body(new RespostaGenerica(resultado));
    }
}

// DTOs devem estar fora da classe
record SolicitarCodigoRequest(String email) {}
record ValidarCodigoRequest(String email, String codigo) {}
record AlterarSenhaRequest(String email, String codigo, String novaSenha) {}
record RespostaGenerica(String mensagem) {}
