package com.proton.services.notificacaoProtocolo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proton.models.entities.ProtocoloNotificacao;
import com.proton.models.repositories.ProtocoloNotificacaoRepository;
import com.proton.services.email.EmailService;

@Service
public class NotificacaoProtocoloService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoProtocoloService.class);
    
    private final EmailService emailService;
    private final ProtocoloNotificacaoRepository protocoloNotificacaoRepository;

    public NotificacaoProtocoloService(EmailService emailService, 
                                     ProtocoloNotificacaoRepository protocoloNotificacaoRepository) {
        this.emailService = emailService;
        this.protocoloNotificacaoRepository = protocoloNotificacaoRepository;
    }

    /**
     * Envia notificação por email sobre atualização de protocolo de forma assíncrona
     * 
     * @param emailDestinatario Email do destinatário
     * @param numeroProtocolo Número do protocolo
     * @param mensagemAtualizacao Mensagem com detalhes da atualização
     * @return CompletableFuture com resultado da operação
     */
    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<String> enviarNotificacaoProtocoloAsync(String emailDestinatario, 
                                                                   String numeroProtocolo, 
                                                                   String mensagemAtualizacao) {
        try {
            // Registra a notificação no banco de dados
            ProtocoloNotificacao notificacao = registrarNotificacao(emailDestinatario, numeroProtocolo, mensagemAtualizacao);
            
            // Monta o assunto e corpo do email
            String assunto = "Atualização no Protocolo #" + numeroProtocolo;
            String corpoEmail = construirCorpoEmail(notificacao, mensagemAtualizacao);
            
            // Envia o email
            emailService.enviarEmailTexto(emailDestinatario, assunto, corpoEmail);
            
            // Atualiza status para enviado
            notificacao.setStatus("ENVIADO");
            protocoloNotificacaoRepository.save(notificacao);
            
            return CompletableFuture.completedFuture("Notificação enviada com sucesso para " + emailDestinatario);
        } catch (Exception e) {
            logger.error("Falha ao enviar notificação para {} - Protocolo {}", emailDestinatario, numeroProtocolo, e);
            
            // Atualiza status da notificação para falha
            atualizarStatusNotificacao(numeroProtocolo, "FALHA", e.getMessage());
            
            return CompletableFuture.completedFuture("Falha ao enviar notificação: " + e.getMessage());
        }
    }

    @Transactional
    protected ProtocoloNotificacao registrarNotificacao(String email, String numeroProtocolo, String mensagem) {
        ProtocoloNotificacao notificacao = new ProtocoloNotificacao();
        notificacao.setEmailDestinatario(email);
        notificacao.setNumeroProtocolo(numeroProtocolo);
        notificacao.setMensagem(mensagem);
        notificacao.setDataEnvio(new Date());
        notificacao.setStatus("PROCESSANDO");
        
        return protocoloNotificacaoRepository.save(notificacao);
    }

    @Transactional
    protected void atualizarStatusNotificacao(String numeroProtocolo, String status, String mensagemErro) {
        protocoloNotificacaoRepository.findTopByNumeroProtocoloOrderByDataEnvioDesc(numeroProtocolo)
            .ifPresent(notificacao -> {
                notificacao.setStatus(status);
                notificacao.setMensagem(mensagemErro);
                protocoloNotificacaoRepository.save(notificacao);
            });
    }

    private String construirCorpoEmail(ProtocoloNotificacao notificacao, String mensagemAtualizacao) {
        StringBuilder sb = new StringBuilder();
        sb.append("Prezado usuário,\n\n");
        sb.append("O protocolo #").append(notificacao.getNumeroProtocolo()).append(" foi atualizado.\n\n");
        sb.append("Detalhes da atualização:\n");
        sb.append(mensagemAtualizacao).append("\n\n");
        sb.append("Data da atualização: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(notificacao.getDataEnvio())).append("\n\n");
        sb.append("Atenciosamente,\n");
        sb.append("Sistema de Protocolos\n");
        
        return sb.toString();
    }

    /**
     * Método síncrono para compatibilidade (usar preferencialmente o assíncrono)
     */
    @Deprecated
    public String enviarNotificacaoProtocolo(String emailDestinatario, String numeroProtocolo, String mensagemAtualizacao) {
        try {
            CompletableFuture<String> resultado = enviarNotificacaoProtocoloAsync(emailDestinatario, numeroProtocolo, mensagemAtualizacao);
            return resultado.get(); // Bloqueia até completar (não recomendado)
        } catch (Exception e) {
            logger.error("Erro no envio síncrono de notificação", e);
            return "Falha ao enviar notificação: " + e.getMessage();
        }
    }

    /**
     * Método para verificar o status de notificações anteriores
     */
    public List<ProtocoloNotificacao> obterHistoricoNotificacoes(String email) {
        return protocoloNotificacaoRepository.findByEmailDestinatarioOrderByDataEnvioDesc(email);
    }
}