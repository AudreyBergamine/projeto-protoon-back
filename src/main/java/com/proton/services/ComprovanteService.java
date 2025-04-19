package com.proton.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.Comprovante;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.StatusComprovante;
import com.proton.models.repositories.ComprovanteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final Path rootLocation;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public ComprovanteService(ComprovanteRepository comprovanteRepository, 
                           @Value("${file.upload-dir}") String uploadDir) {
        this.comprovanteRepository = comprovanteRepository;
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation); // Cria o diretório se não existir
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

    public Comprovante salvarOuAtualizarComprovante(MultipartFile file, Protocolo protocolo) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Falha ao armazenar arquivo vazio");
        }

        // Verifica se já existe um comprovante para este protocolo
        Comprovante comprovanteExistente = comprovanteRepository.findByProtocolo(protocolo)
            .orElse(null);

        // Normaliza o nome do arquivo
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        if (originalFilename.contains("..")) {
            throw new RuntimeException("Nome do arquivo inválido: " + originalFilename);
        }
        
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path destinationFile = this.rootLocation.resolve(filename);
        
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/protoon/comprovantes/download/")
                .path(filename)
                .toUriString();

        Comprovante comprovante;
        
        if (comprovanteExistente != null) {
            // Remove o arquivo antigo
            try {
                Files.deleteIfExists(Paths.get(comprovanteExistente.getCaminhoArquivo()));
            } catch (IOException e) {
                throw new RuntimeException("Falha ao remover arquivo antigo", e);
            }
            
            // Atualiza o comprovante existente
            comprovanteExistente.setNomeArquivo(originalFilename);
            comprovanteExistente.setTipoArquivo(file.getContentType());
            comprovanteExistente.setCaminhoArquivo(destinationFile.toString());
            comprovanteExistente.setUrlDownload(fileDownloadUri);
            comprovanteExistente.setTamanhoArquivo(file.getSize());
            comprovanteExistente.setDataUpload(new Date());
            comprovanteExistente.setStatus(StatusComprovante.PENDENTE);
            
            comprovante = comprovanteExistente;
        } else {
            // Cria um novo comprovante
            comprovante = new Comprovante();
            comprovante.setNomeArquivo(originalFilename);
            comprovante.setTipoArquivo(file.getContentType());
            comprovante.setCaminhoArquivo(destinationFile.toString());
            comprovante.setUrlDownload(fileDownloadUri);
            comprovante.setTamanhoArquivo(file.getSize());
            comprovante.setDataUpload(new Date());
            comprovante.setStatus(StatusComprovante.PENDENTE);
            comprovante.setProtocolo(protocolo);
        }

        return comprovanteRepository.save(comprovante);
    }

    public Resource carregarComprovanteComoResource(String filename) {
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

public Comprovante atualizarStatus(Long id, StatusComprovante status) {
    Comprovante comprovante = comprovanteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Comprovante não encontrado"));
    
    comprovante.setStatus(status);
    
    return comprovanteRepository.save(comprovante);
}
}