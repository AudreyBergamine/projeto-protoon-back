package com.proton.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proton.models.entities.Municipe;
import com.proton.models.entities.RecuperarSenha;
import com.proton.models.repositories.MunicipeRepository;
import com.proton.models.repositories.RecuperarSenhaRepository;

@Service
public class RecuperarSenhaService {

    private static final long TEMPO_EXPIRACAO_MINUTOS = 5;
    
    @Autowired
    private RecuperarSenhaRepository recuperarSenhaRepository;
    
    @Autowired
    private MunicipeRepository municipeRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String enviarCodigoEmail(String email) {
        // Verifica se o email existe no sistema
        Optional<Municipe> municipeOpt = municipeRepository.findByEmail(email);
        if (municipeOpt.isEmpty()) {
            return "Email não encontrado";
        }

        // Gera um código de 6 dígitos
        String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        // Cria ou atualiza o registro de recuperação
        Optional<RecuperarSenha> recuperarSenhaOpt = recuperarSenhaRepository.findByEmail(email);
        RecuperarSenha recuperarSenha = recuperarSenhaOpt.orElse(new RecuperarSenha());
        
        recuperarSenha.setEmail(email);
        recuperarSenha.setCodigo(codigo);
        recuperarSenha.setDateSendCodigo(new Date());
        recuperarSenhaRepository.save(recuperarSenha);

        // Envia o email
        String assunto = "Código de Recuperação de Senha";
        String mensagem = String.format(
            "Seu código de recuperação é: %s\n" +
            "Este código é válido por %d minutos.", 
            codigo, TEMPO_EXPIRACAO_MINUTOS
        );

        try {
            emailService.enviarEmailTexto(email, assunto, mensagem);
            return "Código enviado com sucesso";
        } catch (Exception e) {
            return "Erro ao enviar email: " + e.getMessage();
        }
    }

    public boolean validarCodigo(String email, String codigo) {
        Optional<RecuperarSenha> recuperarSenhaOpt = recuperarSenhaRepository
            .findByEmailAndCodigo(email, codigo);
            
        if (recuperarSenhaOpt.isEmpty()) {
            return false;
        }

        RecuperarSenha recuperarSenha = recuperarSenhaOpt.get();
        long diferencaMinutos = TimeUnit.MILLISECONDS.toMinutes(
            new Date().getTime() - recuperarSenha.getDateSendCodigo().getTime()
        );

        return diferencaMinutos <= TEMPO_EXPIRACAO_MINUTOS;
    }

    public String alterarSenha(String email, String codigo, String novaSenha) {
        if (!validarCodigo(email, codigo)) {
            return "Erro: Código inválido ou expirado";
        }

        Optional<Municipe> municipeOpt = municipeRepository.findByEmail(email);
        if (municipeOpt.isEmpty()) {
            return "Erro: Usuário não encontrado";
        }

        Municipe municipe = municipeOpt.get();
        municipe.setSenha(passwordEncoder.encode(novaSenha));
        municipeRepository.save(municipe);

        // Limpa o código após uso
        RecuperarSenha recuperarSenha = recuperarSenhaRepository.findByEmail(email).get();
        recuperarSenha.setCodigo(null);
        recuperarSenhaRepository.save(recuperarSenha);

        return "Senha alterada com sucesso";
    }
}