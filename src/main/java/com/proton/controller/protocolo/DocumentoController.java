package com.proton.controller.protocolo;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proton.models.entities.protocolo.Documento;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.services.protocolo.DocumentoService;
import com.proton.services.protocolo.ProtocoloService;

@RestController
@RequestMapping("/protoon/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private ProtocoloService protocoloService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @PostMapping("/{protocoloId}/multiplos")
    public ResponseEntity<List<Documento>> uploadMultipleDocumentos(
            @PathVariable Integer protocoloId,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        Protocolo protocolo = protocoloService.findById(protocoloId);
        List<Documento> documentos = documentoService.salvarDocumentos(files, protocolo);
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/{protocoloId}")
    public ResponseEntity<List<Documento>> listarDocumentosPorProtocolo(
            @PathVariable Integer protocoloId) {

        Protocolo protocolo = protocoloService.findById(protocoloId);
        List<Documento> documentos = documentoService.listarDocumentosPorProtocolo(protocolo);
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = documentoService.carregarDocumentoComoResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
