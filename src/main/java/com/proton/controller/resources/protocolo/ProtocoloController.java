package com.proton.controller.resources.protocolo;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.Log;
import com.proton.models.entities.endereco.Endereco;
import com.proton.models.entities.funcionario.Funcionario;
import com.proton.models.entities.municipe.Municipe;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.entities.secretaria.Secretaria;
import com.proton.models.enums.Prioridade;
import com.proton.models.repositories.EnderecoRepository;
import com.proton.models.repositories.FuncionarioRepository;
import com.proton.models.repositories.LogRepository;
import com.proton.models.repositories.MunicipeRepository;
import com.proton.models.repositories.ProtocoloRepository;
import com.proton.models.repositories.SecretariaRepository;
import com.proton.services.Assunto.AssuntoService;
import com.proton.services.notificacaoProtocolo.NotificacaoProtocoloService;
import com.proton.services.protocolo.ProtocoloService;
import com.proton.services.user.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/protoon/protocolo")
public class ProtocoloController {

    @Autowired
    private ProtocoloService protocoloService;

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @Autowired
    private MunicipeRepository municipeRepository;

    @Autowired
    private SecretariaRepository secretariaRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private NotificacaoProtocoloService notificacaoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping(value = "/todos-protocolos") // Adicionando a anotação GetMapping para o método findAll
    public ResponseEntity<List<Protocolo>> findAll() {
        List<Protocolo> list = protocoloService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{numero_protocolo}") // Pesquisa por numero de protocolo
    public ResponseEntity<Protocolo> findByNumeroProtocolo(@PathVariable String numero_protocolo) {
        Optional<Protocolo> protocoloOptional = protocoloRepository.findByNumeroProtocolo(numero_protocolo);
        if (protocoloOptional.isPresent()) {
            Protocolo obj = protocoloService.findByNumero_protocolo(numero_protocolo);
            return ResponseEntity.ok().body(obj);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/pesquisar-municipe/{nomeMunicipe}") // Pesquisar pelo nome do municipe
    public ResponseEntity<List<Protocolo>> findByNomeMunicipe(@PathVariable String nomeMunicipe) {
        List<Protocolo> protocolos = protocoloService.findByNomeMunicipe(nomeMunicipe);
        if (!protocolos.isEmpty()) {
            return ResponseEntity.ok().body(protocolos); // Retorna os protocolos encontrados
        } else {
            return ResponseEntity.notFound().build(); // Retorna status 404 se nenhum protocolo for encontrado
        }
    }

    @GetMapping(value = "/pesquisar-id/{id}") // pesquisar pelo ID
    public ResponseEntity<Protocolo> findById(@PathVariable Integer id) {
        Protocolo obj = protocoloService.findById(id);
        return ResponseEntity.ok().body(obj);// retorna UM protocolo
    }    

    @PostMapping(value = "/abrir-protocolos/{id_secretaria}")
    public ResponseEntity<Protocolo> insertByToken(@RequestBody Protocolo protocolo, @PathVariable Long id_secretaria,
            HttpServletRequest request) {
        Integer id_municipe = authenticationService.getUserIdFromToken(request);
        Municipe mun = municipeRepository.getReferenceById(id_municipe);
        Secretaria sec = secretariaRepository.getReferenceById(id_secretaria);
        Endereco end = enderecoRepository.getReferenceById(mun.getEndereco().getId_endereco());
        String numeroProtocolo = protocoloService.gerarNumeroProtocolo();
        protocolo.setNumero_protocolo(numeroProtocolo);
        protocolo.setMunicipe(mun);
        protocolo.setEndereco(end);
        protocolo.setSecretaria(sec);
        
        // Definir a prioridade com base no assunto
        Prioridade prioridade = assuntoService.determinarPrioridade(protocolo.getAssunto());
        protocolo.setPrioridade(prioridade);
        
        protocoloRepository.save(protocolo);
    
        // Enviar email de notificação
        String mensagemEmail = construirMensagemEmailProtocoloCriado(protocolo, mun);
        notificacaoService.enviarNotificacaoProtocolo(
            mun.getEmail(),
            protocolo.getNumero_protocolo(),
            mensagemEmail
        );
    
        String mensagemLog = String.format(
                "Foi Registrado um novo protocolo: " + protocolo.getNumero_protocolo() + " em %s",
                LocalDateTime.now().format(formatter));
    
        Log log = new Log();
        log.setMensagem(mensagemLog);
        logRepository.save(log);
    
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(protocolo.getId_protocolo()).toUri();
        return ResponseEntity.created(uri).body(protocolo);
    }
    @PostMapping(value = "/abrir-protocolos-reclamar/{id_secretaria}")
public ResponseEntity<Protocolo> insertReclamarByToken(@RequestBody Protocolo protocolo,
        @PathVariable Long id_secretaria,
        HttpServletRequest request) {
    Integer id_m = authenticationService.getUserIdFromToken(request);
    Municipe municipe = municipeRepository.getReferenceById(id_m);
    Secretaria secretaria = secretariaRepository.getReferenceById(id_secretaria);
    Endereco endereco = enderecoRepository.getReferenceById(municipe.getEndereco().getId_endereco());
    String numeroProtocolo = protocoloService.gerarNumeroProtocolo();
    protocolo.setNumero_protocolo(numeroProtocolo);
    protocolo.setMunicipe(municipe);
    protocolo.setEndereco(endereco);
    protocolo.setSecretaria(secretaria);
    
    // Definir a prioridade com base no assunto
    Prioridade prioridade = assuntoService.determinarPrioridade(protocolo.getAssunto());
    protocolo.setPrioridade(prioridade);
    
    LocalDate dataProtocolo = LocalDate.now();
    LocalDate prazoConclusao = dataProtocolo.plusDays(prioridade.getDiasParaResolver());
    protocolo.setPrazoConclusao(prazoConclusao);
    
    protocoloRepository.save(protocolo);

    // Enviar email de notificação
    String mensagemEmail = construirMensagemEmailProtocoloCriado(protocolo, municipe);
    notificacaoService.enviarNotificacaoProtocolo(
        municipe.getEmail(),
        protocolo.getNumero_protocolo(),
        mensagemEmail
    );

    String mensagemLog = String.format(
            "Foi Registrado um novo protocolo: " + protocolo.getNumero_protocolo() + " em %s",
            LocalDateTime.now().format(formatter));

    Log log = new Log();
    log.setMensagem(mensagemLog);
    logRepository.save(log);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(protocolo.getId_protocolo()).toUri();
    return ResponseEntity.created(uri).body(protocolo);
}

@PostMapping(value = "/abrir-protocolos-sem-secretaria")
public ResponseEntity<Protocolo> insertSecretariaNullByToken(@RequestBody Protocolo protocolo,
        HttpServletRequest request) {
    Integer id_municipe = authenticationService.getUserIdFromToken(request);
    Municipe muninicipe = municipeRepository.getReferenceById(id_municipe);
    Endereco endereco = enderecoRepository.getReferenceById(muninicipe.getEndereco().getId_endereco());
    String numeroProtocolo = protocoloService.gerarNumeroProtocolo();
    protocolo.setNumero_protocolo(numeroProtocolo);
    protocolo.setMunicipe(muninicipe);
    protocolo.setEndereco(endereco);
    protocolo.setSecretaria(null);
    
    // Definir a prioridade com base no assunto
    Prioridade prioridade = assuntoService.determinarPrioridade(protocolo.getAssunto());
    protocolo.setPrioridade(prioridade);
    
    protocoloRepository.save(protocolo);

    // Enviar email de notificação
    String mensagemEmail = construirMensagemEmailProtocoloCriado(protocolo, muninicipe);
    notificacaoService.enviarNotificacaoProtocolo(
        muninicipe.getEmail(),
        protocolo.getNumero_protocolo(),
        mensagemEmail
    );

    String mensagemLog = String.format(
            "Foi Registrado um novo protocolo: " + protocolo.getNumero_protocolo() + " em %s",
            LocalDateTime.now().format(formatter));

    Log log = new Log();
    log.setMensagem(mensagemLog);
    logRepository.save(log);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(protocolo.getId_protocolo()).toUri();
    return ResponseEntity.created(uri).body(protocolo);
}

    @PostMapping(value = "/abrir-protocolos/{id_municipe}/{id_secretaria}") // Gera novos protocolos
    public ResponseEntity<Protocolo> insert(@RequestBody Protocolo protocolo, @PathVariable Integer id_municipe,
            @PathVariable Long id_secretaria) {

        Protocolo prot = protocoloService.insert(protocolo, id_municipe, id_secretaria);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(protocolo.getId_protocolo()).toUri();
        return ResponseEntity.created(uri).body(prot);
    }

    @PutMapping("/alterar-protocolos/status/{numero_protocolo}") // Altera os protocolos (TODO REVER ISSO DEPOIS)
    public ResponseEntity<Protocolo> update(@PathVariable String numero_protocolo, @RequestBody Protocolo protocolo,
            HttpServletRequest request) {
        Integer id_funcionario = authenticationService.getUserIdFromToken(request);
        Funcionario funcionario = funcionarioRepository.findById(id_funcionario)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Protocolo obj = protocoloService.updateStatus(numero_protocolo, protocolo, funcionario.getNome());
        Municipe muninicipe = obj.getMunicipe();
    // Enviar email de notificação
    String mensagemEmail = construirMensagemEmailProtocoloCriado(protocolo, muninicipe);
    notificacaoService.enviarNotificacaoProtocolo(
        muninicipe.getEmail(),
        protocolo.getNumero_protocolo(),
        mensagemEmail
    );
        return ResponseEntity.ok(obj);
    }

    @PutMapping("/alterar-protocolos/departamento/{numero_protocolo}") // Altera os protocolos (TODO REVER ISSO DEPOIS)
    public ResponseEntity<Protocolo> updateRedirect(@PathVariable String numero_protocolo,
            @RequestBody Protocolo protocolo, HttpServletRequest request) {

        Integer id_funcionario = authenticationService.getUserIdFromToken(request);
        Funcionario funcionario = funcionarioRepository.findById(id_funcionario)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Protocolo obj = protocoloService.updateRedirect(numero_protocolo, protocolo, funcionario.getNome());
        return ResponseEntity.ok(obj);
    }

    @PutMapping("/alterar-protocolos/valor/{numero_protocolo}")
    public ResponseEntity<?> updateValor(@PathVariable String numero_protocolo,
            @RequestBody Protocolo protocolo, HttpServletRequest request) {
        try {
            Integer id_funcionario = authenticationService.getUserIdFromToken(request);
            Funcionario funcionario = funcionarioRepository.findById(id_funcionario)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

            Protocolo obj = protocoloService.updateValor(numero_protocolo, protocolo, funcionario.getNome());
            return ResponseEntity.ok(obj);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar protocolo: " + e.getMessage());
        }
    }

    @GetMapping(value = "/meus-protocolos/bytoken") // Pesquisa os protocolos do munipe logado
    public ResponseEntity<List<Protocolo>> findByIdMunicipe(HttpServletRequest request) {
        // Extração do ID do munícipe autenticado pelo TOKEN (Atualização para a
        // segurança do site)
        // Validação para ver se o TOKEN foi recebido msm
        Integer id = authenticationService.getUserIdFromToken(request);
        if (id != null) {
            Optional<Municipe> municipeOptional = municipeRepository.findById(id);
            if (municipeOptional.isPresent()) {
                Municipe municipe = municipeOptional.get();
                // Usa o ID do municipe recuperado ali em cima para buscar os protocolos, igual
                // antes
                List<Protocolo> protocolos = protocoloService.findByMunicipe(municipe);
                return ResponseEntity.ok().body(protocolos);// retorna VARIOS protocolos do MUNICIPE LOGADO
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String construirMensagemEmailProtocoloCriado(Protocolo protocolo, Municipe municipe) {
        return String.format(
            """
            Prezado(a) %s,

            Protocolo #%s criado
            Assunto: %s
            ✔️ Prioridade: %s
            ✔️ Data: %s

            Você pode acompanhar o andamento pelo nosso sistema.

            Atenciosamente,
            PROTO-ON - Protocolos Municipais 💜
            """,
            municipe.getNome(), // Nome do Usuário
            protocolo.getNumero_protocolo(),
            protocolo.getAssunto(),
            protocolo.getPrioridade().toString(),
            LocalDateTime.now().format(formatter)
        );
    }
}
