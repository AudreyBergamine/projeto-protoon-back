package com.proton.models.entities.AI;

import com.proton.services.AnaliseIA.AIAnalysisException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Classe para padronização de respostas de análise de IA
 */
public class AnalysisResponse {
    private final boolean success;
    private final String analysis;
    private final String error;
    private final String errorCode;
    private final int itemsProcessed;
    private final long timestamp;
    private final String formattedDate;

    // Construtor para respostas de sucesso
    public AnalysisResponse(String analysis, int itemsProcessed) {
        this.success = true;
        this.analysis = analysis;
        this.itemsProcessed = itemsProcessed;
        this.error = null;
        this.errorCode = null;
        this.timestamp = System.currentTimeMillis();
        this.formattedDate = formatTimestamp(this.timestamp);
    }

    // Construtor para respostas de erro
    public AnalysisResponse(AIAnalysisException e) {
        this.success = false;
        this.error = e.getMessage();
        this.errorCode = e.getErrorCode();
        this.analysis = null;
        this.itemsProcessed = 0;
        this.timestamp = System.currentTimeMillis();
        this.formattedDate = formatTimestamp(this.timestamp);
    }

    private String formatTimestamp(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(timestamp));
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getAnalysis() {
        return analysis;
    }

    public String getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getItemsProcessed() {
        return itemsProcessed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    @Override
    public String toString() {
        return "AnalysisResponse{" +
                "success=" + success +
                ", analysisLength=" + (analysis != null ? analysis.length() : 0) +
                ", error='" + error + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", itemsProcessed=" + itemsProcessed +
                ", timestamp=" + formattedDate +
                '}';
    }
}