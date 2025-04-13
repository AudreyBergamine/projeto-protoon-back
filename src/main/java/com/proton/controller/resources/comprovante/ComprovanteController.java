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
            "✔️ Prioridade: %s\n" +
            "✔️ Status: %s\n" +
            "✔️ Link para baixar a imagem: %s\n" +

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
            LocalDateTime.now().format(formatter)


            
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


}