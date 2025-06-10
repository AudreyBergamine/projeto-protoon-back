package com.proton.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.Comprovante;
import com.proton.models.entities.Municipe;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.entities.redirecionamento.Redirecionamento;
import com.proton.services.NotificacaoProtocoloService;
import com.proton.services.RedirecionamentoService;
import com.proton.services.user.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/protoon/redirecionamento")
public class RedirecionamentoController {

    @Autowired
    RedirecionamentoService service;

    @Autowired
    AuthenticationService authenticationService;


    @Autowired
    private NotificacaoProtocoloService notificacaoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping
    public ResponseEntity<List<Redirecionamento>> findAll() {
        List<Redirecionamento> list = service.findAll();

        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Redirecionamento> findById(@PathVariable Integer id) {
        Redirecionamento redirecionamento = service.findById(id);

        return ResponseEntity.ok().body(redirecionamento);
    }

    @GetMapping("/funcionario")
    public ResponseEntity<List<Redirecionamento>> findByFuncionarioId(HttpServletRequest request) {
        Integer id_fun = authenticationService.getUserIdFromToken(request);

        return ResponseEntity.ok().body(service.findByIdFuncionario(id_fun));
    }

    //TODO VAGNER CRIAR REDIRECIONAMENTO
    @PostMapping("/{id_prot}")
    public ResponseEntity<Redirecionamento> insertByToken(@RequestBody Redirecionamento redirecionamento,
            HttpServletRequest request, @PathVariable Integer id_prot) {
        Integer id_fun = authenticationService.getUserIdFromToken(request); //Id já atrelado no frontend
    
        Redirecionamento redSaved = service.insert(redirecionamento, id_fun, id_prot);
        
        Protocolo protocolo = redSaved.getProtocolo(); 
        Municipe municipe = protocolo.getMunicipe(); 
        
        // Construir e enviar notificação
        String mensagem = construirMensagemProtocoloRedirecionado(
            protocolo, 
            municipe,
            redSaved
        );
        
        notificacaoService.enviarNotificacaoProtocolo(
            municipe.getEmail(),
            protocolo.getNumero_protocolo(),
            mensagem
        );
    
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(redirecionamento.getId()).toUri();
        return ResponseEntity.created(uri).body(redSaved);
    }

    @PutMapping("/by-coordenador/{id_red}")
    public ResponseEntity<Redirecionamento> updateByCoordenador(@RequestBody Redirecionamento redirecionamento,
            @PathVariable Integer id_red, HttpServletRequest request) {
        Integer id_fun = authenticationService.getUserIdFromToken(request);
        Redirecionamento redSaved = service.updateByCoordenador(id_red, redirecionamento, id_fun);
        System.out.println(id_red);
        return ResponseEntity.ok().body(redSaved);

    }

    // Atualizar por lote
    @PutMapping("/by-coordenador")
    public ResponseEntity<List<Redirecionamento>> updateByCoordenadorList(
            @RequestBody List<Redirecionamento> redirecionamentos,
            HttpServletRequest request) {
        Integer id_fun = authenticationService.getUserIdFromToken(request);
        List<Redirecionamento> redirecionamentosAtualizados = service.updateByCoordenador(redirecionamentos, id_fun);
        return ResponseEntity.ok().body(redirecionamentosAtualizados);
    }

    private String construirMensagemProtocoloRedirecionado(Protocolo protocolo, Municipe municipe, Redirecionamento redirecionamento) {
        return String.format(
            """
            Prezado(a) %s,
    
            Seu protocolo #%s foi redirecionado.
    
            Nova secretaria responsável: %s
            Data: %s
            Assunto: %s
            Prioridade: %s
    
            Observações: %s
    
            Você pode acompanhar o andamento pelo nosso sistema.
    
            Atenciosamente,
            PROTO-ON - Protocolos Municipais 💜
            """,
            municipe.getNome(),
            protocolo.getNumero_protocolo(),
            redirecionamento.getNovaSecretaria() != null ? redirecionamento.getNovaSecretaria() : "Não informada",
            LocalDateTime.now().format(formatter),
            protocolo.getAssunto() != null ? protocolo.getAssunto() : "Não informado",
            protocolo.getPrioridade() != null ? protocolo.getPrioridade().toString() : "Não definida",
            redirecionamento.getDescricao() != null ? redirecionamento.getDescricao() : "Sem observações"
        );
    }


   
}
