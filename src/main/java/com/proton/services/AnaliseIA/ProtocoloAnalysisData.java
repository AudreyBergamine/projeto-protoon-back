package com.proton.services.AnaliseIA;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.Status;

public class ProtocoloAnalysisData {
    private final int totalProtocolos;
    private final int protocolosAtrasados;
    private final Map<String, Long> protocolosPorSecretaria;
    private final Map<Status, Long> protocolosPorStatus;
    private final List<Protocolo> protocolos;

    public ProtocoloAnalysisData(List<Protocolo> protocolos) {
        if (protocolos == null) {
            throw new IllegalArgumentException("Lista de protocolos não pode ser nula");
        }
        
        this.totalProtocolos = protocolos.size();
        this.protocolosAtrasados = calcularProtocolosAtrasados(protocolos);
        this.protocolosPorSecretaria = agruparPorSecretaria(protocolos);
        this.protocolosPorStatus = agruparPorStatus(protocolos);
        this.protocolos = protocolos;
    }

    private int calcularProtocolosAtrasados(List<Protocolo> protocolos) {
        return (int) protocolos.stream()
                .filter(p -> p.getPrazoConclusao() != null &&
                           p.getPrazoConclusao().isBefore(LocalDate.now()) &&
                           p.getStatus() != Status.CONCLUIDO)
                .count();
    }

    private Map<String, Long> agruparPorSecretaria(List<Protocolo> protocolos) {
        return protocolos.stream()
                .filter(p -> p.getSecretaria() != null)
                .collect(Collectors.groupingBy(
                    p -> p.getSecretaria().getNome_secretaria(),
                    Collectors.counting()
                ));
    }

    private Map<Status, Long> agruparPorStatus(List<Protocolo> protocolos) {
        return protocolos.stream()
                .filter(p -> p.getStatus() != null) 
                .collect(Collectors.groupingBy(
                    Protocolo::getStatus,
                    Collectors.counting()
                ));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== RESUMO ESTATÍSTICO ===\n");
        sb.append("Total de Protocolos: ").append(totalProtocolos).append("\n");
        sb.append("Protocolos Atrasados: ").append(protocolosAtrasados).append("\n");
        sb.append("Percentual de Atraso: ")
          .append(totalProtocolos > 0 ? 
                 String.format("%.1f%%", (protocolosAtrasados * 100.0 / totalProtocolos)) : "0%")
          .append("\n\n");
        
        if (!protocolosPorSecretaria.isEmpty()) {
            sb.append("=== DISTRIBUIÇÃO POR SECRETARIA ===\n");
            protocolosPorSecretaria.forEach((secretaria, quantidade) -> 
                sb.append("- ").append(secretaria).append(": ").append(quantidade).append("\n"));
            sb.append("\n");
        }
        
        if (!protocolosPorStatus.isEmpty()) {
            sb.append("=== DISTRIBUIÇÃO POR STATUS ===\n");
            protocolosPorStatus.forEach((status, quantidade) -> 
                sb.append("- ").append(status).append(": ").append(quantidade).append("\n"));
            sb.append("\n");
        }
        
        sb.append("=== DETALHES DOS PROTOCOLOS ===\n");
        protocolos.forEach(p -> {
            sb.append("ID: ").append(p.getId_protocolo()).append("\n");
            sb.append("Assunto: ").append(p.getAssunto()).append("\n");
            sb.append("Status: ").append(p.getStatus()).append("\n");
            sb.append("Prioridade: ").append(p.getPrioridade()).append("\n");
            
            if (p.getPrazoConclusao() != null) {
                sb.append("Prazo: ").append(p.getPrazoConclusao()).append("\n");
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), p.getPrazoConclusao());
                sb.append("Dias Restantes: ").append(diasRestantes).append("\n");
            } else {
                sb.append("Prazo: Não definido\n");
            }
            
            sb.append("---\n");
        });
        
        return sb.toString();
    }
}