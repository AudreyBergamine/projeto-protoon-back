package com.proton.controller.protocolo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proton.models.entities.Municipe;
import com.proton.models.entities.protocolo.Devolutiva;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.repositories.DevolutivaRepository;
import com.proton.models.repositories.FuncionarioRepository;
import com.proton.services.NotificacaoProtocoloService;
import com.proton.services.protocolo.DevolutivaService;
import com.proton.services.protocolo.ProtocoloService;
import com.proton.services.user.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/protoon/devolutiva")
public class DevolutivaController {

    @Autowired
    private DevolutivaService devolutivaService;

    @Autowired
    private DevolutivaRepository devolutivaRepository;

    @Autowired
    private FuncionarioRepository fun;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private NotificacaoProtocoloService notificacaoService;

    @Autowired
    private ProtocoloService protocoloService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping
    public ResponseEntity<List<Devolutiva>> findAll() {
        List<Devolutiva> devolutivas = devolutivaService.findAll();
        if (!devolutivas.isEmpty()) {
            return ResponseEntity.ok(devolutivas);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Devolutiva> findDevolutivaById(@PathVariable Long id) {
        Devolutiva devolutiva = devolutivaService.findById(id);
        if (devolutiva != null) {
            return ResponseEntity.ok(devolutiva);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/criar-devolutiva/{id_Protocolo}")
    public ResponseEntity<Devolutiva> insertDevolutiva(@RequestBody Devolutiva devolutiva,
            @PathVariable Integer id_Protocolo,
            HttpServletRequest request) {
        Integer id_funcionario = authenticationService.getUserIdFromToken(request);
        Long id_secretaria = fun.findBySecretaria(id_funcionario);
        if (id_funcionario != null) {
            Devolutiva insertDevolutiva = devolutivaService.insert(devolutiva, id_funcionario, id_Protocolo,
                    id_secretaria);

            Protocolo protocolo = protocoloService.findById(id_Protocolo);
            Municipe muninicipe = protocolo.getMunicipe();
            String mensagemEmail = construirMensagemEmailDevolutivaCriada(protocolo, muninicipe, insertDevolutiva);
            notificacaoService.enviarNotificacaoProtocolo(
                    muninicipe.getEmail(),
                    protocolo.getNumero_protocolo(),
                    mensagemEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(insertDevolutiva);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/criar-devolutiva-boleto")
    public ResponseEntity<Devolutiva> insertDevolutivaBoleto(@RequestBody Protocolo protocolo) {
        // Criar a devolutiva apenas se o protocolo NÃO estiver cancelado
        if (!protocolo.getStatus().equals("CANCELADO")) {
            Devolutiva devolutiva = new Devolutiva(null, null, protocolo, Instant.now(),
                    "O prazo de pagamento do boleto venceu ✖️");

            // Salvar a devolutiva no banco
            Devolutiva savedDevolutiva = devolutivaRepository.save(devolutiva);

            // Retornar a devolutiva criada com status 201 CREATED
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDevolutiva);
        }

        // Retorno caso o protocolo esteja cancelado
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null); // Pode substituir null por uma devolutiva vazia ou uma mensagem
    }

    @GetMapping("/devolutiva-protocolo/{id_protocolo}") // retorna todas as devoluções de um protocolo, TODO organizar
                                                        // esse retorno.
    public ResponseEntity<List<Devolutiva>> findDevolutivasByProtocolo(@PathVariable int id_protocolo) {
        List<Devolutiva> devolutivas = devolutivaService.findByIdProtocolo(id_protocolo);
        // System.out.println("VALOR AQUI: " + devolutivas);
        if (devolutivas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devolutivas);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deleteDevolutiva(@PathVariable Long id) {
        try {
            devolutivaService.deleteDevolutiva(id);
            return ResponseEntity.ok("Devolutiva deletada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao deletar devolutiva: " + e.getMessage());
        }
    }

    private String construirMensagemEmailDevolutivaCriada(Protocolo protocolo, Municipe municipe,
            Devolutiva devolutiva) {
        return String.format(
                """
                        Prezado(a) %s,
                        O status do seu protocolo Nº #%s foi atualizado.

                        Devolutiva #%s criada
                        ✔️ Assunto: %s
                        ✔️ Prioridade: %s
                        ✔️ Data: %s
                        ✔️ Prazo para Conclusão ou Parecer: %s

                        Você pode acompanhar o andamento pelo nosso sistema.
                        Atenciosamente,
                        PROTO-ON - Protocolos Municipais 💜
                        """,
                municipe.getNome(), // %s 1 (Nome)
                protocolo.getNumero_protocolo(), // %s 2 (Nº Protocolo)
                devolutiva.getDevolutiva(), // %s 3 (Devolutiva)
                protocolo.getAssunto(), // %s 4 (Assunto) - Corrigido
                protocolo.getPrioridade().toString(), // %s 5 (Prioridade)
                LocalDateTime.now().format(formatter), // %s 6 (Data)
                protocolo.getPrazoConclusao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }
}
