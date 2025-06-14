package com.proton.config;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.proton.models.entities.Assunto;
import com.proton.models.entities.Endereco;
import com.proton.models.entities.Funcionario;
import com.proton.models.entities.Municipe;
import com.proton.models.entities.Secretaria;
import com.proton.models.entities.protocolo.Devolutiva;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.enums.Prioridade;
import com.proton.models.enums.Role;
import com.proton.models.enums.Status;
import com.proton.models.repositories.AssuntoRepository;
import com.proton.models.repositories.DevolutivaRepository;
import com.proton.models.repositories.EnderecoRepository;
import com.proton.models.repositories.FuncionarioRepository;
import com.proton.models.repositories.MunicipeRepository;
import com.proton.models.repositories.ProtocoloRepository;
import com.proton.models.repositories.SecretariaRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

        @Autowired
        private EnderecoRepository enderecoRepository;

        @Autowired
        private MunicipeRepository municipeRepository;

        @Autowired
        private FuncionarioRepository funcionarioRepository;

        @Autowired
        private SecretariaRepository secretariaRepository;

        @Autowired
        private ProtocoloRepository protocoloRepository;

        @Autowired
        private AssuntoRepository assuntoRepository;

        @Autowired
        private DevolutivaRepository devolutivaRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                // 1. Inicializar e salvar endereços
                List<Endereco> enderecos = criarEnderecos();
                enderecoRepository.saveAll(enderecos);

                // 2. Inicializar e salvar secretarias
                List<Secretaria> secretarias = criarSecretarias(enderecos);
                secretariaRepository.saveAll(secretarias);

                // 3. Inicializar e salvar municipes
                List<Municipe> municipes = criarMunicipes(enderecos);
                municipeRepository.saveAll(municipes);

                // 4. Inicializar e salvar assuntos
                List<Assunto> assuntos = criarAssuntos(secretarias);
                assuntoRepository.saveAll(assuntos);

                // 5. Criar e salvar protocolos setando a quantidade
                List<Protocolo> protocolos = criarProtocolos(50, secretarias, municipes, enderecos, assuntos);
                protocoloRepository.saveAll(protocolos);

                List<Funcionario> funcionarios = criarFuncionariosPersonalizados(secretarias);
                funcionarioRepository.saveAll(funcionarios);

                // 6. Criar e salvar devolutiva referenciando o protocolo
                Devolutiva dev1 = new Devolutiva(
                                null, // ID gerado automaticamente
                                null, // Funcionário (pode ser null se não houver)
                                protocolos.get(0), // Protocolo associado
                                Instant.now(), // Data/hora atual
                                "Protocolo recebido e em análise" // Mensagem inicial
                );
                devolutivaRepository.save(dev1);
        }

        private List<Endereco> criarEnderecos() {
                return Arrays.asList(
                                // Apartamento no Centro
                                new Endereco(null, "apartamento", "08500-000", "Rua São Paulo", "Apto 101",
                                                "100", "Edifício Central", "Centro", "Ferraz de Vasconcelos", "SP",
                                                "Brasil"),

                                // Casa no Jardim Pérola
                                new Endereco(null, "casa", "08503-000", "Rua das Flores", "",
                                                "250", "", "Jardim Pérola", "Ferraz de Vasconcelos", "SP", "Brasil"),

                                // Comércio na Avenida Brasil
                                new Endereco(null, "comércio", "08510-000", "Avenida Brasil", "Loja 5",
                                                "500", "Galeria Comercial", "Vila Romanópolis", "Ferraz de Vasconcelos",
                                                "SP", "Brasil"),

                                // Casa no Parque São Francisco
                                new Endereco(null, "casa", "08520-000", "Rua dos Ipês", "",
                                                "75", "", "Parque São Francisco", "Ferraz de Vasconcelos", "SP",
                                                "Brasil"),

                                // Sobrado no Jardim Promissão
                                new Endereco(null, "sobrado", "08530-000", "Rua das Palmeiras", "Sobrado 2",
                                                "30", "", "Jardim Promissão", "Ferraz de Vasconcelos", "SP", "Brasil"),

                                // Kitnet no Centro
                                new Endereco(null, "kitnet", "08500-100", "Rua Minas Gerais", "Kitnet 3",
                                                "300", "Residencial Sol Nascente", "Centro", "Ferraz de Vasconcelos",
                                                "SP", "Brasil"),

                                // Casa no Jardim Primavera
                                new Endereco(null, "casa", "08540-000", "Rua das Hortênsias", "",
                                                "80", "", "Jardim Primavera", "Ferraz de Vasconcelos", "SP", "Brasil"));
        }

        private List<Secretaria> criarSecretarias(List<Endereco> enderecos) {
                return Arrays.asList(
                                new Secretaria(null, "Secretaria de Educação", "Ana Silva", "ana@email.com",
                                                enderecos.get(1)),
                                new Secretaria(null, "Secretaria de Saúde", "Carlos Santos", "carlos@email.com",
                                                enderecos.get(2)),
                                new Secretaria(null, "Secretaria de Meio Ambiente", "Mariana Oliveira",
                                                "mariana@email.com", enderecos.get(0)));
        }

        private List<Municipe> criarMunicipes(List<Endereco> enderecos) {
                String senha = passwordEncoder.encode("123456");

                Municipe mun1 = new Municipe("Fulano", "fulano@email.com", senha, "973.087.140-04",
                                "(11)96256-8965", LocalDate.of(1990, 5, 15), enderecos.get(4));
                mun1.setRole(Role.MUNICIPE);

                Municipe mun2 = new Municipe("Ciclano", "ciclano@email.com", senha, "699.367.750-40",
                                "(11)96256-8965", LocalDate.of(1985, 10, 25), enderecos.get(1));
                mun2.setRole(Role.MUNICIPE);

                Municipe secretario = new Municipe("Secretário", "secretario@email.com", senha, "999.654.321-00",
                                "(11)96256-8965", LocalDate.of(1995, 03, 12), enderecos.get(3));
                secretario.setRole(Role.SECRETARIO);

                return Arrays.asList(mun1, mun2, secretario);
        }

        private List<Funcionario> criarFuncionariosPersonalizados(List<Secretaria> secretarias) {
    String senhaCriptografada = passwordEncoder.encode("123456");

    Endereco enderecoNovo = new Endereco(
        null,
        "Residencial",
        "08541-000",
        "Estrada",
        "Casa",
        "1611",
        "", // ponto de referência
        "Cháracara Laguna",
        "Ferraz de Vasconcelos",
        "SP",
        "Brasil"
    );

      Endereco enderecoNovo2 = new Endereco(
        null,
        "Residencial",
        "08541-000",
        "Estrada",
        "Casa",
        "1612",
        "", // ponto de referência
        "Cháracara Laguna",
        "Poá",
        "SP",
        "Brasil"
    );

    Funcionario funcionario = Funcionario.builder()
        .nome("ProtonOn Roberto")
        .email("funcionario@gmail.com")
        .senha(senhaCriptografada)
        .role(Role.FUNCIONARIO)
        .num_CPF("026.479.380-32")
        .data_nascimento(LocalDate.of(2024, 3, 9))
        .celular("978742743")
        .numTelefoneFixo(null)
        .endereco(enderecoNovo)
        .secretaria(secretarias.get(0)) // Secretaria de Educação
        .build();

    Funcionario coordenador = Funcionario.builder()
        .nome("Coordenador Silva")
        .email("coordenador@gmail.com")
        .senha(senhaCriptografada)
        .role(Role.COORDENADOR)
        .num_CPF("105.222.333-44")
        .data_nascimento(LocalDate.of(1985, 8, 20))
        .celular("979991122")
        .numTelefoneFixo(null)
        .endereco(enderecoNovo2) // mesmo endereço
        .secretaria(secretarias.get(0)) // mesma secretaria
        .build();

    return Arrays.asList(funcionario, coordenador);
}

        private List<Assunto> criarAssuntos(List<Secretaria> secretarias) {
                Secretaria secEducacao = secretarias.get(0); // Secretaria de Educação
                Secretaria secSaude = secretarias.get(1); // Secretaria de Saúde
                Secretaria secMeioAmb = secretarias.get(2); // Secretaria de Meio Ambiente

                return Arrays.asList(
                                new Assunto(null, "Iluminação Pública", secMeioAmb, 150.00, Prioridade.BAIXA, 7),
                                new Assunto(null, "Buraco na Via", secMeioAmb, 80.00, Prioridade.ALTA, 3),
                                new Assunto(null, "Coleta de Lixo", secMeioAmb, 0.00, Prioridade.MEDIA, 5),
                                new Assunto(null, "Podas de Árvores", secMeioAmb, 90.00, Prioridade.MEDIA, 5),
                                new Assunto(null, "Reparo de Calçadas", secMeioAmb, 75.00, Prioridade.MEDIA, 5),
                                new Assunto(null, "Manutenção de Parque", secMeioAmb, 95.00, Prioridade.BAIXA, 7),
                                new Assunto(null, "Falta de professores", secEducacao, 100.00, Prioridade.URGENTE, 1),
                                new Assunto(null, "Merenda escolar insuficiente", secEducacao, 85.00, Prioridade.ALTA,
                                                3),
                                new Assunto(null, "Transporte escolar atrasado", secEducacao, 75.00, Prioridade.ALTA,
                                                3),
                                new Assunto(null, "Infraestrutura precária na escola", secEducacao, 120.00,
                                                Prioridade.MEDIA, 5),
                                new Assunto(null, "Falta de médicos em unidade de saúde", secSaude, 120.00,
                                                Prioridade.URGENTE, 1),
                                new Assunto(null, "Demora no atendimento em hospital", secSaude, 90.00, Prioridade.ALTA,
                                                3),
                                new Assunto(null, "Falta de medicamentos", secSaude, 100.00, Prioridade.ALTA, 3),
                                new Assunto(null, "Agendamento de consulta médica", secSaude, 40.00, Prioridade.MEDIA,
                                                5),
                                new Assunto(null, "Solicitação de transferência escolar", secEducacao, 55.00,
                                                Prioridade.BAIXA, 7),
                                new Assunto(null, "Outros", null, 0.00, Prioridade.BAIXA, 1));
        }

        private List<Protocolo> criarProtocolos(int quantidade, List<Secretaria> secretarias,
                        List<Municipe> municipes, List<Endereco> enderecos,
                        List<Assunto> assuntos) {
                List<Protocolo> protocolos = new ArrayList<>();
                Random random = new Random();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String anoAtual = sdf.format(new Date());

                // Mapeamento de descrições para cada assunto
                Map<String, String> descricoes = Map.ofEntries(
                                Map.entry("Iluminação Pública",
                                                "Lâmpada queimada na via pública necessitando substituição"),
                                Map.entry("Buraco na Via", "Buraco de aproximadamente 50cm de diâmetro na pista"),
                                Map.entry("Coleta de Lixo", "Lixo acumulado há mais de 3 dias sem coleta"),
                                Map.entry("Podas de Árvores", "Galhos de árvore obstruindo calçada e via pública"),
                                Map.entry("Reparo de Calçadas", "Calçada danificada com desnível perigoso"),
                                Map.entry("Manutenção de Parque", "Equipamentos de playground necessitando reparos"),

                                Map.entry("Falta de professores", "Turmas sem aula por ausência de professores"),
                                Map.entry("Merenda escolar insuficiente",
                                                "Quantidade de merenda abaixo da demanda dos alunos"),
                                Map.entry("Transporte escolar atrasado",
                                                "Ônibus escolar com recorrentes atrasos nos horários"),
                                Map.entry("Infraestrutura precária na escola",
                                                "Problemas como rachaduras, goteiras ou falta de ventilação nas salas"),
                                Map.entry("Solicitação de transferência escolar",
                                                "Pais ou responsáveis solicitam mudança de escola para o aluno"),

                                Map.entry("Falta de médicos em unidade de saúde",
                                                "Posto de saúde operando sem número adequado de médicos"),
                                Map.entry("Demora no atendimento em hospital",
                                                "Tempo de espera excessivo para atendimento emergencial"),
                                Map.entry("Falta de medicamentos",
                                                "Medicamentos básicos em falta na farmácia da unidade"),
                                Map.entry("Agendamento de consulta médica",
                                                "Dificuldade ou impossibilidade de marcar consulta pelo sistema"),

                                Map.entry("Outros", "Demanda não listada nos assuntos padrão"));

                // Data aleatória entre jun/2025 e hoje
                LocalDate inicio = LocalDate.of(2025, 6, 1);
                LocalDate hoje = LocalDate.now();

                for (int i = 1; i <= quantidade; i++) {
                        // Seleciona aleatoriamente os componentes do protocolo
                        Assunto assunto = assuntos.get(random.nextInt(assuntos.size()));
                        Secretaria secretaria = assunto.getSecretaria();
                        Municipe municipe = municipes.get(random.nextInt(municipes.size()));
                        Endereco endereco = enderecos.get(random.nextInt(enderecos.size()));

                        // Obtém a descrição correspondente ao assunto
                        String descricao = descricoes.get(assunto.getAssunto());

                        // Obter todos os valores do enum Status
                        Status[] todosStatus = Status.values();

                        Status status;
                        if (assunto.getPrioridade() == Prioridade.URGENTE) {
                                status = Status.EM_ANDAMENTO;
                        } else if (assunto.getAssunto() == "Outros") {
                                status = Status.EM_ANALISE;
                        } else {
                                // Filtra status não permitidos para protocolos não urgentes, se necessário
                                status = todosStatus[random.nextInt(todosStatus.length)];
                        }

                        // Gera data aleatória diretamente como LocalDate
                        LocalDate dataProtocolo = gerarDataAleatoria(inicio, hoje);

                        // Verificação e atualização de status para CANCELADO
                        if (status == Status.PAGAMENTO_PENDENTE) {
                                LocalDate tresDiasAtras = LocalDate.now().minusDays(3);

                                if (dataProtocolo.isBefore(tresDiasAtras) && assunto.getValor_protocolo() != 0) {
                                        status = Status.CANCELADO;
                                }
                        }

                        // Calcula prazo CORRETAMENTE adicionando os dias do assunto
                        LocalDate prazoConclusao = dataProtocolo.plusDays(assunto.getTempoResolucao());

                        // Converte para Date apenas se necessário (para manter compatibilidade)
                        Date dataProtocoloDate = Date
                                        .from(dataProtocolo.atStartOfDay(ZoneId.systemDefault()).toInstant());

                        // Formata número do protocolo
                        String numero = String.format("%03d", i) + "-" + anoAtual;

                        // Cria o protocolo
                        Protocolo protocolo = new Protocolo(
                                        null,
                                        secretaria,
                                        municipe,
                                        endereco,
                                        assunto.getAssunto(),
                                        dataProtocoloDate,
                                        descricao,
                                        status,
                                        assunto.getValor_protocolo(),
                                        numero,
                                        prazoConclusao);

                        protocolo.setPrioridade(assunto.getPrioridade());

                        protocolos.add(protocolo);
                }

                return protocolos;
        }

        private LocalDate gerarDataAleatoria(LocalDate inicio, LocalDate fim) {
                long dias = ChronoUnit.DAYS.between(inicio, fim);
                long diasAleatorios = (long) (Math.random() * dias);
                return inicio.plusDays(diasAleatorios);
        }
}