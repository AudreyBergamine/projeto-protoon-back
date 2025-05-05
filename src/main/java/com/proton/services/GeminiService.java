package com.proton.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proton.models.entities.GeminiRequest;
import com.proton.models.entities.GeminiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeminiService {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public GeminiService(
            RestTemplate restTemplate,
            @Value("${google.gemini.api.url}") String apiUrl,
            @Value("${google.gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public String generateContent(String prompt) {
        try {
            GeminiRequest request = new GeminiRequest();
            GeminiRequest.Content content = new GeminiRequest.Content();
            GeminiRequest.Part part = new GeminiRequest.Part();
            part.setText(prompt);
            content.setParts(List.of(part));
            request.setContents(List.of(content));

            String url = apiUrl + "?key=" + apiKey;

            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, createHeaders()),
                    GeminiResponse.class);

            return extractResponseText(response.getBody());
        } catch (Exception e) {
            log.error("Erro ao chamar Gemini API", e);
            return "Erro: " + e.getMessage();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String extractResponseText(GeminiResponse response) {
        if (response != null && 
            response.getCandidates() != null && 
            !response.getCandidates().isEmpty()) {
            return response.getCandidates().get(0)
                          .getContent()
                          .getParts()
                          .get(0)
                          .getText();
        }
        return "Não foi possível gerar uma resposta.";
    }
}