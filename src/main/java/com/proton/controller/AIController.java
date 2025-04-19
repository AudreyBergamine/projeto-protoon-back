package com.proton.controller;

import com.proton.models.entities.AnalysisResponse;
import com.proton.models.entities.protocolo.Devolutiva;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.services.AnaliseIA.AIAnalysisException;
import com.proton.services.AnaliseIA.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analise")
public class AIController {
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Endpoint para análise de protocolos
     * 
     * @param protocolos Lista de protocolos para análise
     * @return ResponseEntity com o resultado da análise ou erro
     */
    @PostMapping("/protocolos")
    public ResponseEntity<?> analisarProtocolos(@RequestBody List<Protocolo> protocolos) {
        final String operation = "analysis/protocolos";
        logger.info("Iniciando análise para {} protocolos", protocolos.size());

        try {
            String analysis = aiService.generateAnalysis(protocolos);
            return buildSuccessResponse(analysis, protocolos.size());
        } catch (AIAnalysisException e) {
            logger.error("Erro na análise de protocolos - Código: {}", e.getErrorCode(), e);
            return buildErrorResponse(e, operation);
        }
    }

    /**
     * Endpoint para análise de devolutivas
     * 
     * @param devolutivas Lista de devolutivas para análise
     * @return ResponseEntity com o resultado da análise ou erro
     */
    @PostMapping("/devolutivas")
    public ResponseEntity<AnalysisResponse> analisarDevolutivas(@RequestBody List<Devolutiva> devolutivas) {
        final String operation = "analysis/devolutivas";
        logger.info("Iniciando análise para {} devolutivas", devolutivas.size());

        try {
            String analysis = aiService.analisarDevolutivas(devolutivas);
            return ResponseEntity.ok(new AnalysisResponse(analysis, devolutivas.size()));
        } catch (AIAnalysisException e) {
            logger.error("Erro na análise de devolutivas - Código: {}", e.getErrorCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnalysisResponse(e.getMessage(), 0)); // Usando construtor existente
        }
    }

    /**
     * Endpoint para análise de sentimento em texto livre
     * 
     * @param texto Texto para análise de sentimento
     * @return ResponseEntity com o resultado da análise ou erro
     */
    @PostMapping("/sentimento")
    public ResponseEntity<AnalysisResponse> analisarSentimento(@RequestBody String texto) {
        final String operation = "analysis/sentimento";
        logger.info("Iniciando análise de sentimento para texto com {} caracteres", texto.length());

        try {
            String analysis = aiService.analisarSentimento(texto);
            return ResponseEntity.ok(new AnalysisResponse(analysis, 1));
        } catch (AIAnalysisException e) {
            logger.error("Erro na análise de sentimento - Código: {}", e.getErrorCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnalysisResponse(e.getMessage(), 0)); // Usando construtor existente
        }
    }

    // ========== Métodos Auxiliares ========== //

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(String analysis, int itemsProcessed) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "analysis", analysis,
                "itemsProcessed", itemsProcessed,
                "timestamp", Instant.now().toString()));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(AIAnalysisException e, String operation) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", "error",
                        "errorCode", e.getErrorCode(),
                        "operation", operation,
                        "message", e.getMessage(),
                        "timestamp", Instant.now().toString()));
    }
}