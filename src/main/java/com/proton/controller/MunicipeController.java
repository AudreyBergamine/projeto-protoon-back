package com.proton.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;  >> NÃO VAI DELETAR USUÁRIO
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.Log;
import com.proton.models.entities.Municipe;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.repositories.LogRepository;
import com.proton.services.MunicipeService;
import com.proton.services.user.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "protoon/municipe/municipes")
@CrossOrigin(origins = "http://localhost:3000")
public class MunicipeController {

    @Autowired // Para que o Spring faça essa injeção de Dependência do Service
    private MunicipeService service; // Dependência para o Service

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private LogRepository logRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // TODO Remover acesso a lista completa! E retirar retorno do atributo senha!

    // Método que responde á requisição do tipo GET do HTTP
    @GetMapping
    public ResponseEntity<List<Municipe>> findAll() {
        List<Municipe> list = service.findAll();
        return ResponseEntity.ok().body(list);

    }

    @GetMapping(value = "/bytoken")
    public ResponseEntity<Municipe> findById(HttpServletRequest request) {
        Integer id = authService.getUserIdFromToken(request);
        Municipe obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping(value = "/{id}") // A requisição vai aceitar um ID dentro do URL
    public ResponseEntity<Municipe> findById(@PathVariable Integer id) {
        Municipe obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    // Método que responde á requisição do tipo POST do HTTP
    @PostMapping
    public ResponseEntity<Municipe> insert(@RequestBody Municipe obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId())
                .toUri();

        String mensagemLog = String.format("Foi cadastrado um novo Municípe: " + obj.getEmail() + " em %s",
                LocalDateTime.now().format(formatter));

        Log log = new Log();
        log.setMensagem(mensagemLog);
        logRepository.save(log);

        return ResponseEntity.created(uri).body(obj); // Código 201
    }

    // TODO NÃO TERÁ DELETE PARA O USUÁRIO >>>> REGRA DE NEGÓCIO
    // @DeleteMapping(value = "/{id}")
    // public ResponseEntity<Void> delete(@PathVariable Integer id){
    // service.delete(id);
    // return ResponseEntity.noContent().build(); //Resposta para sem conteúdo,
    // código 204
    // }

    @PutMapping(value = "/bytoken") // A requisição vai aceitar um ID dentro do URL
    public ResponseEntity<Municipe> update(HttpServletRequest request, @RequestBody Municipe obj) {
        Integer id = authService.getUserIdFromToken(request);
        obj = service.update(id, obj);

        String mensagemLog = String.format("Foi Atualizado os dados do Municípe: " + obj.getEmail() + " em %s",
                LocalDateTime.now().format(formatter));

        Log log = new Log();
        log.setMensagem(mensagemLog);
        logRepository.save(log);

        return ResponseEntity.ok(obj);
    }

    @GetMapping(value = "/protocolos/{id}")
    public ResponseEntity<List<Protocolo>> findProtocolosByMunicipeId(@PathVariable Integer id) {
        Municipe mun = service.findById(id);
        return ResponseEntity.ok().body(mun.getProtocolos());
    }

    @GetMapping(value = "/protocolos/bytoken")
    public ResponseEntity<List<Protocolo>> findProtocolosByToken(HttpServletRequest request) {
        Integer id = authService.getUserIdFromToken(request);
        Municipe mun = service.findById(id);
        return ResponseEntity.ok().body(mun.getProtocolos());

    }

}
