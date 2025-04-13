package com.proton.services.AnaliseIA;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.proton.models.entities.protocolo.Devolutiva;
import com.proton.models.entities.protocolo.Protocolo;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private static final int MAX_PROMPT_LENGTH = 2000;
    private static final int MODEL_LOAD_WAIT_TIME_MS = 5000;

    @Value("${huggingface.api.url}")
    private String hfApiUrl;

    @Value("${huggingface.api.key}")
    private String hfApiKey;

    private final CloseableHttpClient httpClient;
    private final Gson gson;
    private final Map<String, String> analysisCache;

    public AIService() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
        this.analysisCache = new HashMap<>();
    }

    // ========== Métodos Públicos Principais ========== //

    private HttpPost createApiRequest(String prompt) throws AIAnalysisException {
        try {
            HttpPost httpPost = new HttpPost(hfApiUrl);
            httpPost.setHeader("Authorization", "Bearer " + hfApiKey);
            httpPost.setHeader("Content-Type", "application/json");

            // Formato compatível com a API Hugging Face
            Map<String, Object> requestBody = new HashMap<>();

            // Opção 1: Para modelos que aceitam texto direto
            requestBody.put("inputs", prompt);

            // Opção 2: Para modelos que exigem estrutura complexa (ex: summarization)
            /*
             * Map<String, Object> inputs = new HashMap<>();
             * inputs.put("text", prompt);
             * inputs.put("parameters", Map.of("max_length", 200));
             * requestBody.put("inputs", inputs);
             */

            String jsonPayload = gson.toJson(requestBody);
            logger.debug("Payload enviado: {}", jsonPayload);
            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            return httpPost;
        } catch (Exception e) {
            throw new AIAnalysisException("Falha ao formatar requisição", e, "REQUEST_FORMAT_ERROR");
        }
    }

    private String queryHuggingFaceAPI(String prompt) throws AIAnalysisException {
        try {
            HttpPost httpPost = createApiRequest(prompt);

            // Configurar timeout
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(30000)
                    .setConnectionRequestTimeout(30000)
                    .setSocketTimeout(30000)
                    .build();
            httpPost.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                validateApiResponse(response);
                String responseBody = EntityUtils.toString(response.getEntity());
                logger.debug("Resposta da API: {}", responseBody);
                return processAIResponse(responseBody);
            }
        } catch (IOException e) {
            throw new AIAnalysisException("Falha na comunicação com a API", e, "API_COMMUNICATION_ERROR");
        }
    }

    private void validateApiResponse(CloseableHttpResponse response) throws IOException, AIAnalysisException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            String errorResponse = EntityUtils.toString(response.getEntity());
            logger.error("Erro na API - Status: {} - Resposta: {}", statusCode, errorResponse);

            // Tratamento específico para erros conhecidos
            if (statusCode == 400) {
                throw new AIAnalysisException("Formato de requisição inválido", "API_FORMAT_ERROR");
            } else if (statusCode == 503) {
                throw new AIAnalysisException("Modelo não carregado ou indisponível", "MODEL_UNAVAILABLE");
            } else {
                throw new AIAnalysisException("Erro na API: " + statusCode, "API_" + statusCode);
            }
        }
    }

    public String generateAnalysis(List<Protocolo> protocolos) throws AIAnalysisException {
        validateProtocolList(protocolos);
        String dadosFormatados = formatProtocolDataForAnalysis(protocolos);
        String prompt = buildAnalysisPrompt(dadosFormatados);

        logger.debug("Enviando prompt para Hugging Face API...");
        return queryHuggingFaceAPI(prompt);
    }

    public String analisarDevolutivas(List<Devolutiva> devolutivas) throws AIAnalysisException {
        validateDevolutivasList(devolutivas);
        String dadosFormatados = formatDevolutivaData(devolutivas);
        String prompt = buildDevolutivaPrompt(dadosFormatados);
        return queryHuggingFaceAPI(prompt);
    }

    public String analisarSentimento(String texto) throws AIAnalysisException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new AIAnalysisException("Texto para análise não pode ser vazio", "SENTIMENT_001");
        }
        String prompt = buildSentimentPrompt(texto);
        return queryHuggingFaceAPI(prompt);
    }

    // ========== Métodos de Comunicação com a API ========== //

    private String executeApiRequest(HttpPost request) throws IOException, AIAnalysisException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            validateApiResponse(response);
            return processAIResponse(EntityUtils.toString(response.getEntity()));
        }
    }

    private String processAIResponse(String jsonResponse) throws AIAnalysisException {
        try {
            JsonObject responseJson = gson.fromJson(jsonResponse, JsonObject.class);

            if (responseJson.has("error")) {
                throw new AIAnalysisException("Erro na API: " + responseJson.get("error").getAsString(), "API_002");
            }

            return extractGeneratedText(responseJson);
        } catch (JsonSyntaxException e) {
            throw new AIAnalysisException("Resposta da API em formato inválido", e, "API_003");
        }
    }

    private String extractGeneratedText(JsonObject responseJson) throws AIAnalysisException {
        if (responseJson.has("generated_text")) {
            return responseJson.get("generated_text").getAsString();
        }

        if (responseJson.has("0") && responseJson.get("0").isJsonObject()) {
            JsonObject firstResult = responseJson.get("0").getAsJsonObject();
            if (firstResult.has("generated_text")) {
                return firstResult.get("generated_text").getAsString();
            }
        }

        throw new AIAnalysisException("Formato de resposta não reconhecido", "API_004");
    }

    // ========== Métodos de Formatação ========== //

    private String formatDevolutivaData(List<Devolutiva> devolutivas) {
        DevolutivaAnalysisData data = new DevolutivaAnalysisData(devolutivas);
        return data.toString();
    }

    private String formatProtocolDataForAnalysis(List<Protocolo> protocolos) {
        ProtocoloAnalysisData data = new ProtocoloAnalysisData(protocolos);
        return data.toString();
    }

    private String truncatePrompt(String prompt) {
        return prompt.length() > MAX_PROMPT_LENGTH
                ? prompt.substring(0, MAX_PROMPT_LENGTH) + "... [TRUNCATED]"
                : prompt;
    }

    // ========== Métodos de Validação ========== //

    private void validateProtocolList(List<Protocolo> protocolos) throws AIAnalysisException {
        if (protocolos == null || protocolos.isEmpty()) {
            throw new AIAnalysisException("Lista de protocolos inválida", "VAL_001");
        }

        List<String> errors = protocolos.stream()
                .filter(p -> p.getSecretaria() == null || p.getSecretaria().getNome_secretaria() == null)
                .map(p -> "Protocolo " + p.getId_protocolo() + " sem secretaria válida")
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new AIAnalysisException("Erros de validação:\n" + String.join("\n", errors), "VAL_002");
        }
    }

    private void validateDevolutivasList(List<Devolutiva> devolutivas) throws AIAnalysisException {
        if (devolutivas == null || devolutivas.isEmpty()) {
            throw new AIAnalysisException("Lista de devolutivas inválida", "VAL_003");
        }

        List<String> errors = new ArrayList<>();
        Set<Integer> protocolosProcessados = new HashSet<>();

        for (Devolutiva dev : devolutivas) {
            if (dev.getDevolutiva() == null || dev.getDevolutiva().trim().isEmpty()) {
                errors.add("Devolutiva sem conteúdo");
            }
            if (dev.getId_funcionario() == null) {
                errors.add("Devolutiva sem funcionário responsável");
            }
            if (dev.getId_protocolo() == null) {
                errors.add("Devolutiva sem protocolo vinculado");
            } else if (!protocolosProcessados.add(dev.getId_protocolo().getId_protocolo())) {
                errors.add("Protocolo " + dev.getId_protocolo().getId_protocolo() + " com múltiplas devolutivas");
            }
            if (dev.getMomento_devolutiva() == null || dev.getMomento_devolutiva().isAfter(Instant.now())) {
                errors.add("Data/hora da devolutiva inválida");
            }
        }

        if (!errors.isEmpty()) {
            throw new AIAnalysisException("Erros na validação:\n" + String.join("\n", errors), "VAL_004");
        }
    }

    // ========== Métodos Auxiliares ========== //

    private String generateCacheKey(String prompt) {
        return String.valueOf(prompt.hashCode());
    }

    private String buildAnalysisPrompt(String dadosFormatados) {
        return """
                **Análise de Protocolos Municipais**

                Por favor, analise os seguintes dados e gere um relatório detalhado com:

                1. **Visão Geral**
                2. **Análise Detalhada**
                3. **Recomendações**
                4. **Alertas Críticos**

                Dados:
                %s

                Formato requerido: Markdown
                """.formatted(dadosFormatados);
    }

    private String buildDevolutivaPrompt(String dadosFormatados) {
        return """
                **Análise de Devolutivas**

                Avalie:
                - Qualidade das respostas
                - Eficiência no atendimento
                - Clareza e objetividade

                Dados:
                %s

                Formato: Markdown com exemplos
                """.formatted(dadosFormatados);
    }

    private String buildSentimentPrompt(String texto) {
        return """
                **Análise de Sentimento**

                Classifique:
                - Sentimento (Positivo/Neutro/Negativo)
                - Tom da linguagem
                - Qualidade da resposta

                Texto para análise:
                %s
                """.formatted(texto);
    }
}

// Classes auxiliares para formatação de dados (implementar conforme
// necessidade)
class ProtocoloAnalysisData {
    private final List<Protocolo> protocolos;

    public ProtocoloAnalysisData(List<Protocolo> protocolos) {
        this.protocolos = protocolos;
    }

    @Override
    public String toString() {
        // Implementar formatação dos dados dos protocolos
        return ""; // Retornar string formatada
    }
}

class DevolutivaAnalysisData {
    private final List<Devolutiva> devolutivas;

    public DevolutivaAnalysisData(List<Devolutiva> devolutivas) {
        this.devolutivas = devolutivas;
    }

    @Override
    public String toString() {
        // Implementar formatação dos dados das devolutivas
        return ""; // Retornar string formatada
    }
}