package com.proton.controller.resources.comprovante;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.proton.models.entities.comprovante.Comprovante;
import com.proton.models.entities.municipe.Municipe;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.Status;
import static com.proton.models.enums.Status.CONCLUIDO;
import com.proton.models.enums.StatusComprovante;
import com.proton.services.comprovante.ComprovanteService;
import com.proton.services.notificacaoProtocolo.NotificacaoProtocoloService;
import com.proton.services.protocolo.ProtocoloService;

@RestController
@RequestMapping("/protoon/comprovantes")
public class ComprovanteController {
    
    @Autowired
    private ComprovanteService comprovanteService;

    @Autowired
    private ProtocoloService protocoloService;

    @Autowired
    private NotificacaoProtocoloService notificacaoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @PostMapping("/{protocoloId}")
    public ResponseEntity<Comprovante> uploadComprovante(
            @PathVariable Integer protocoloId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        Protocolo protocolo = protocoloService.findById(protocoloId);
        Comprovante comprovante = comprovanteService.salvarOuAtualizarComprovante(file, protocolo);
        Municipe muninicipe = protocolo.getMunicipe();
        String mensagemEmail = construirMensagemEmailComprovanteCriado(protocolo, muninicipe, comprovante);
        notificacaoService.enviarNotificacaoProtocolo(
                muninicipe.getEmail(),
                protocolo.getNumero_protocolo(),
                mensagemEmail);
        return ResponseEntity.ok(comprovante);
    }
    
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = comprovanteService.carregarComprovanteComoResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Comprovante> atualizarStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusParam) {
    
        if (statusParam == null || statusParam.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "O campo 'status' não pode ser vazio");
        }
    
        try {
            StatusComprovante status = StatusComprovante.valueOf(statusParam.toUpperCase());
            Comprovante comprovante = comprovanteService.atualizarStatus(id, status);

            Protocolo protocolo = comprovante.getProtocolo();
            Municipe muninicipe = protocolo.getMunicipe();
            String mensagemEmail = construirMensagemEmailComprovanteAtualizado(protocolo, muninicipe, comprovante);
            notificacaoService.enviarNotificacaoProtocolo(
                    muninicipe.getEmail(),
                    protocolo.getNumero_protocolo(),
                    mensagemEmail);
            return ResponseEntity.ok(comprovante);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Status inválido. Valores permitidos: " + Arrays.toString(StatusComprovante.values()));
        }
    }




        private String construirMensagemEmailComprovanteCriado(Protocolo protocolo, Municipe municipe, Comprovante comprovante) {
        return String.format(
            "Comprovante #%s criado\n\n" + // Get Nome do Usuário
            "Seu comprovante para o protocolo #%s foi registrado com sucesso!\n\n" + // Get Número do Protocolo
            "Detalhes do comprovante:\n" +
            "✔️ Número: %d\n" + // Get ID do Comprovante
            "✔️ Data/hora do registro: %s\n" + // Get Data de Upload
            "✔️ Status inicial: %s\n\n" + // Get Status do Comprovante
            "✔️ Prioridade: %s\n" + // Get Prioridade do Comprovante
            "✔️ Status: %s\n" + // Get Status do Comprovante
            "✔️ Data: %s\n" + // Get Data de Upload
            "✔️ Link para baixar a imagem: %s\n" + // Get URL para download do Comprovante

            "Você pode acompanhar o andamento pelo nosso sistema.\n\n" + 
            "Atenciosamente,\n" + 
            "PROTO-ON - Protocolos Municipais 💜 \n\n" +

            municipe.getNome(), //Nome do Usuário
            protocolo.getNumero_protocolo(), // Número do Protocolo
            comprovante.getDataUpload(),
            // LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            comprovante.getId(), // ID do Comprovante
            comprovante.getPrioridade(), // Prioridade do Comprovante
            comprovante.getStatus(), // Status do Comprovante
            // comprovante.getPrioridade(), // Prioridade do Comprovante 
            comprovante.getUrlDownload(),
            LocalDateTime.now().format(formatter),
            
            //  Nova função para obter a mensagem de status by Audrey
            getMensagemStatus(protocolo)


            
        );
    }

    private String construirMensagemEmailComprovanteAtualizado(Protocolo protocolo, Municipe municipe, Comprovante comprovante) {
        // Acesse o protocolo diretamente do comprovante
        Protocolo protocoloDoComprovante = comprovante.getProtocolo();
        
        return String.format(
            "Prezado(a) %s,\n\n" + // Get Nome do Usuário
            "O status do seu protocolo Nº #%s foi atualizado.\n\n" + // Get Número do Protocolo
            "Detalhes da atualização:\n" +
            // "Protocolo: %s\n" +
            "✔️ Comprovante #%s criado\n" +
            "✔️ Prioridade: %s\n" +
            "✔️ Status: %s\n" +
            "✔️ Data: %s" +
            "✔️ Link para baixar a imagem: %s\n" +

            "Você pode acompanhar o andamento pelo nosso sistema.\n\n" + 
            "Atenciosamente,\n" + 
            "PROTO-ON - Protocolos Municipais 💜 \n\n",

            municipe.getNome(), // Nome do Usuário
            protocoloDoComprovante.getNumero_protocolo(), // Número do Protocolo
            comprovante.getId(), // ID do Comprovante
            comprovante.getPrioridade(), // Prioridade do Comprovante
            comprovante.getStatus(), // Status do Comprovante
            comprovante.getDataUpload(),
            LocalDateTime.now().format(formatter), // Data de Upload
            comprovante.getUrlDownload() // URL para download do Comprovante
        );
    }

    private String getMensagemStatus(Protocolo protocolo) {
        switch(protocolo.getStatus()) {
            case PAGAMENTO_PENDENTE:
                return "Atenção: Seu protocolo aguarda pagamento. " +
                       "Para dar continuidade ao processo, por favor, realize o pagamento conforme as instruções enviadas. " +
                       "O protocolo só será analisado após a confirmação do pagamento.";
                
            case CIENCIA:
                return "Seu protocolo foi recebido e está em análise inicial pela equipe técnica. " +
                       "Você será notificado quando houver atualizações. " +
                       "Caso necessário, podemos solicitar informações adicionais.";
                
            case CIENCIA_ENTREGA:
                return "Seu protocolo está em fase de análise e entrega simultaneamente. " +
                       "Nossa equipe está verificando a documentação enquanto prepara os materiais para entrega. " +
                       "Você será notificado quando o processo for concluído.";
                
            case CONCLUIDO:
                return "Seu protocolo foi concluído com sucesso! " +
                       "Agradecemos seu contato e ficamos à disposição para novas solicitações. " +
                       "Caso tenha alguma dúvida sobre o serviço prestado, entre em contato conosco.";
                
            case CANCELADO:
                return "Seu protocolo foi cancelado conforme solicitado. " +
                       "Caso tenha sido um engano ou queira reabrir o processo, entre em contato conosco " +
                       "dentro do prazo de 5 dias úteis.";
                
            case RECUSADO:
                return "Seu protocolo foi recusado após análise. " +
                       "Você receberá em breve as justificativas detalhadas para a recusa. " +
                       "Caso discorde da decisão, pode entrar com um recurso no prazo de 10 dias úteis.";
                
            default:
                return "Seu protocolo está em andamento em nosso sistema. " +
                       "Acompanhe as atualizações ou entre em contato conosco para mais informações.";
        }
    }

    public String getMensagemPadrao(Protocolo protocolo) {
        switch(protocolo.getStatus()) {
            case PAGAMENTO_PENDENTE: return "Aguardando pagamento...";
            default: return "Status atual: Informação indisponível.";
        }
    }

    public String getCorStatus(Protocolo protocolo) {
        switch(protocolo.getStatus()) {
            case CONCLUIDO: return "#28a745"; // Verde
            case RECUSADO: return "#dc3545";  // Vermelho
            default: return "#6c757d"; // Cinza
        }
    }

}