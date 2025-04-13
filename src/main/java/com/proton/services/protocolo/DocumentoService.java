package com.proton.services.protocolo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.protocolo.Documento;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.repositories.DocumentoRepository;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final Path rootLocation;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository,
            @Value("${file.upload-dir}") String uploadDir) {
        this.documentoRepository = documentoRepository;
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar o diretório de upload", e);
        }
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar o diretório de upload", e);
        }
    }


    // Salva múltiplos arquivos vinculados a um protocolo

    public List<Documento> salvarDocumentos(MultipartFile[] files, Protocolo protocolo) throws IOException {
        List<Documento> documentosSalvos = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Documento documento = salvarDocumento(file, protocolo);
                documentosSalvos.add(documento);
            }
        }

        return documentosSalvos;
    }


     // Método para salvar um único documento
    private Documento salvarDocumento(MultipartFile file, Protocolo protocolo) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Falha ao armazenar arquivo vazio");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFilename.contains("..")) {
            throw new RuntimeException("Nome do arquivo inválido: " + originalFilename);
        }

        // Gera um nome único para o arquivo
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path destinationFile = this.rootLocation.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/protoon/documentos/download/")
                .path(filename)
                .toUriString();

        // Cria um novo documento
        Documento documento = new Documento();
        documento.setNomeArquivo(originalFilename);
        documento.setTipoArquivo(file.getContentType());
        documento.setCaminhoArquivo(destinationFile.toString());
        documento.setUrlDownload(fileDownloadUri);
        documento.setTamanhoArquivo(file.getSize());
        documento.setDataUpload(new Date());
        documento.setProtocolo(protocolo);

        return documentoRepository.save(documento);
    }

    
    // Lista todos os documentos de um protocolo

    public List<Documento> listarDocumentosPorProtocolo(Protocolo protocolo) {
        return documentoRepository.findByProtocolo(protocolo);
    }


    // Carrega um documento como recurso para download

    public Resource carregarDocumentoComoResource(String filename) {
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Arquivo não encontrado: " + filename);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao carregar o arquivo: " + filename, ex);
        }
    }
}