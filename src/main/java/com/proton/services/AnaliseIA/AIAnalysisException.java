package com.proton.services.AnaliseIA;

public class AIAnalysisException extends Exception {
    private final String errorCode;

    // Construtor básico
    public AIAnalysisException(String message) {
        super(message);
        this.errorCode = "IA_000";
    }

    // Construtor com código de erro customizado
    public AIAnalysisException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // Construtor com causa
    public AIAnalysisException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "IA_000";
    }

    // Construtor completo
    public AIAnalysisException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // Getter para o código de erro
    public String getErrorCode() {
        return errorCode;
    }
}