package com.proton.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proton.models.entities.assunto.Assunto;
import com.proton.models.entities.endereco.Endereco;
import com.proton.models.entities.municipe.Municipe;
import com.proton.models.entities.protocolo.Devolutiva;
import com.proton.models.entities.protocolo.Protocolo;
import com.proton.models.entities.secretaria.Secretaria;

import com.proton.models.enums.Prioridade;
import com.proton.models.enums.Role;
import com.proton.models.enums.Status;

import com.proton.models.repositories.AssuntoRepository;
import com.proton.models.repositories.DevolutivaRepository;
import com.proton.models.repositories.EnderecoRepository;
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
        public void run(String... args) throws Exception {
                String senha = passwordEncoder.encode("123456");

                Endereco end1 = new Endereco(null, "apartamento", "54321-876", "Avenida Secundária", "Casa 202",
                                "456", "Edifício B", "Bairro Novo", "Rio de Janeiro", "RJ", "Brasil");

                Endereco end2 = new Endereco(null, "escritório", "98765-432", "Praça Central", "Sala 301",
                                "789", "Torre C", "Centro Histórico", "Porto Alegre", "RS", "Brasil");

                Endereco end3 = new Endereco(null, "casa", "11111-222", "Rua dos Fundos", "Casa 303",
                                "101", "Bloco C", "Periferia", "Belo Horizonte", "MG", "Brasil");
                Endereco end4 = new Endereco(null, "casa", "11111-222", "Rua dos Fundos", "Casa 303",
                                "101", "Bloco C", "Periferia", "Belo Horizonte", "MG", "Brasil");
                Endereco end5 = new Endereco(null, "casa", "11111-222", "Rua dos Fundos", "Casa 303",
                                "101", "Bloco C", "Periferia", "Belo Horizonte", "MG", "Brasil");

                Endereco end6 = new Endereco(null, "Rua Dois", "11111-222", "Rua dos Fundos", "Casa 303",
                                "101", "Bloco C", "Periferia", "Belo Horizonte", "MG", "Brasil");

                Secretaria secEducacao = new Secretaria(null, "Secretaria de Educação", "Ana Silva", "ana@email.com",
                                end2);

                Secretaria secSaude = new Secretaria(null, "Secretaria de Saúde", "Carlos Santos", "carlos@email.com",
                                end3);

                Secretaria secMeioAmb = new Secretaria(null, "Secretaria de Meio Ambiente", "Mariana Oliveira",
                                "mariana@email.com", end1);

                Municipe mun1 = new Municipe("Fulano", "fulano@email.com", senha, "973.087.140-04",
                                "(11)96256-8965",
                                LocalDate.of(1990, 5, 15), end5);
                mun1.setRole(Role.MUNICIPE);

                Municipe mun2 = new Municipe("Ciclano", "ciclano@email.com", senha, "699.367.750-40",
                                "(11)96256-8965",
                                LocalDate.of(1985, 10, 25), end2);
                mun2.setRole(Role.MUNICIPE);

                Municipe secretario = new Municipe("Secretário", "secretario@email.com", senha, "999.654.321-00",
                                "(11)96256-8965",
                                LocalDate.of(1985, 10, 25), end4);
                secretario.setRole(Role.SECRETARIO);

                // Data atual do protocolo
                Date dataProtocolo = new Date();

                // Converter data do protocolo para LocalDate
                LocalDate dataProtocoloLocalDate = Instant.ofEpochMilli(dataProtocolo.getTime())
                                .atZone(ZoneId.systemDefault()).toLocalDate();

                // Adicionar 7 dias
                LocalDate prazo1 = dataProtocoloLocalDate.plusDays(7);
                LocalDate prazo2 = dataProtocoloLocalDate.plusDays(5);
                LocalDate prazo3 = dataProtocoloLocalDate.plusDays(3);

                // // Converter de volta para Date
                // long prazoConclusao1 = ChronoUnit.DAYS.between(LocalDate.now(), prazo1);
                // long prazoConclusao2 = ChronoUnit.DAYS.between(LocalDate.now(), prazo2);
                // long prazoConclusao3 = ChronoUnit.DAYS.between(LocalDate.now(), prazo3);

                Protocolo prot1 = new Protocolo(null, secSaude, mun1, end2, "Assunto do protocolo", new Date(),
                                "Descrição do protocolo", Status.CIENCIA, 100.0, "001-2025",
                                prazo2);

                Protocolo prot2 = new Protocolo(null, secEducacao, mun2, end3, "Outro assunto", new Date(),
                                "Outra descrição",
                                Status.PAGAMENTO_PENDENTE,
                                150.0, "002-2025", prazo1);

                Protocolo prot3 = new Protocolo(null, secMeioAmb, mun2, end3, "Teste", new Date(), "teste",
                                Status.CONCLUIDO,
                                150.0, "003-2025", prazo3);

                Protocolo prot4 = new Protocolo(null, secMeioAmb, mun1, end2, "Assunto do protocolo", new Date(),
                                "Descrição do protocolo", Status.CIENCIA, 100.0, "004-2025", prazo2);

                Protocolo prot5 = new Protocolo(null, secSaude, mun1, end2, "Assunto do protocolo", new Date(),
                                "Descrição do protocolo", Status.CIENCIA, 100.0, "005-2025", prazo1);

                Protocolo prot6 = new Protocolo(null, secSaude, mun1, end2, "Assunto do protocolo", new Date(),
                                "Descrição do protocolo", Status.CIENCIA, 100.0, "006-2025", prazo3);

                Protocolo prot7 = new Protocolo(null, secEducacao, mun2, end3, "Reforma escolar", new Date(124, 0, 15),
                                "Reforma da escola municipal", Status.CIENCIA, 25000.0, "007-2025", prazo1);
                Protocolo prot8 = new Protocolo(null, secSaude, mun1, end2, "Compra de medicamentos",
                                new Date(124, 0, 16), "Aquisição de remédios para posto", Status.PAGAMENTO_PENDENTE,
                                15000.0, "008-2025", prazo2);
                Protocolo prot9 = new Protocolo(null, secMeioAmb, mun2, end3, "Plantio de árvores",
                                new Date(124, 0, 17), "Reflorestamento área urbana", Status.CONCLUIDO, 5000.0,
                                "009-2025", prazo3);
                Protocolo prot10 = new Protocolo(null, secEducacao, mun1, end1, "Pavimentação rua A",
                                new Date(124, 0, 18),
                                "Asfaltamento da rua principal", Status.CIENCIA, 120000.0, "010-2025", prazo1);

                Protocolo prot11 = new Protocolo(null, secMeioAmb, mun1, end4, "Distribuição de sementes",
                                new Date(124, 0, 19), "Apoio a agricultores familiares", Status.CIENCIA, 8000.0,
                                "011-2025", prazo2);
                Protocolo prot12 = new Protocolo(null, secSaude, mun1, end2, "Cesta básica",
                                new Date(124, 0, 20), "Distribuição de alimentos", Status.CONCLUIDO, 20000.0,
                                "012-2025", prazo3);
                Protocolo prot13 = new Protocolo(null, secEducacao, mun2, end3, "Compra de livros",
                                new Date(124, 0, 21), "Aquisição para biblioteca", Status.PAGAMENTO_PENDENTE, 10000.0,
                                "013-2025", prazo1);
                Protocolo prot14 = new Protocolo(null, secSaude, mun1, end2, "Campanha vacinação", new Date(124, 0, 22),
                                "Vacinação contra influenza", Status.CIENCIA, 15000.0, "014-2025", prazo2);
                Protocolo prot15 = new Protocolo(null, secMeioAmb, mun1, end4, "Coleta seletiva", new Date(124, 0, 23),
                                "Implantação de programa", Status.CIENCIA, 30000.0, "015-2025", prazo3);

                Protocolo prot16 = new Protocolo(null, secEducacao, mun2, end3, "Construção de ponte",
                                new Date(124, 0, 24), "Ponte sobre o rio X", Status.CIENCIA, 500000.0, "016-2025",
                                prazo1);
                Protocolo prot17 = new Protocolo(null, secMeioAmb, mun1, end1, "Curso agricultura orgânica",
                                new Date(124, 0, 25), "Capacitação para produtores", Status.CONCLUIDO, 5000.0,
                                "017-2025", prazo2);
                Protocolo prot18 = new Protocolo(null, secSaude, mun1, end4, "Abrigo temporário",
                                new Date(124, 0, 26), "Abrigo para população em situação de rua", Status.CIENCIA,
                                40000.0, "018-2025", prazo3);
                Protocolo prot19 = new Protocolo(null, secEducacao, mun1, end2, "Tablets para alunos",
                                new Date(124, 0, 27), "Aquisição de dispositivos", Status.PAGAMENTO_PENDENTE, 60000.0,
                                "019-2025", prazo1);
                Protocolo prot20 = new Protocolo(null, secSaude, mun2, end3, "Equipamento hospitalar",
                                new Date(124, 0, 28), "Compra de aparelhos médicos", Status.CIENCIA, 250000.0,
                                "020-2025", prazo2);

                // Continuação com meses variados...
                Protocolo prot21 = new Protocolo(null, secMeioAmb, mun1, end2, "Recuperação de nascente",
                                new Date(124, 1, 1), "Proteção de nascente urbana", Status.CIENCIA, 15000.0,
                                "021-2025", prazo3);
                Protocolo prot22 = new Protocolo(null, secMeioAmb, mun1, end4, "Drenagem pluvial", new Date(124, 1, 5),
                                "Obras de drenagem no bairro Y", Status.CONCLUIDO, 80000.0, "022-2025", prazo1);
                Protocolo prot23 = new Protocolo(null, secSaude, mun2, end3, "Feira do produtor",
                                new Date(124, 1, 10), "Organização de feira semanal", Status.CIENCIA, 3000.0,
                                "023-2025", prazo2);
                Protocolo prot24 = new Protocolo(null, secEducacao, mun1, end2, "Bolsa família",
                                new Date(124, 1, 15), "Processamento de benefícios", Status.CIENCIA, 120000.0,
                                "024-2025", prazo3);
                Protocolo prot25 = new Protocolo(null, secEducacao, mun1, end4, "Reforma quadra esportiva",
                                new Date(124, 1, 20), "Melhorias na quadra da escola", Status.PAGAMENTO_PENDENTE,
                                35000.0, "025-2025", prazo1);

                Protocolo prot26 = new Protocolo(null, secSaude, mun1, end2, "Contratação de médicos",
                                new Date(124, 2, 3), "Processo seletivo para PSF", Status.CIENCIA, 200000.0, "026-2025",
                                prazo2);
                Protocolo prot27 = new Protocolo(null, secMeioAmb, mun2, end3, "Educação ambiental",
                                new Date(124, 2, 8), "Palestras em escolas", Status.CONCLUIDO, 5000.0, "027-2025",
                                prazo3);
                Protocolo prot28 = new Protocolo(null, secMeioAmb, mun1, end1, "Iluminação pública",
                                new Date(124, 2, 13),
                                "Substituição de luminárias", Status.CIENCIA, 90000.0, "028-2025", prazo1);
                Protocolo prot29 = new Protocolo(null, secSaude, mun1, end4, "Apoio à apicultura",
                                new Date(124, 2, 18), "Distribuição de kits apícolas", Status.CIENCIA, 12000.0,
                                "029-2025", prazo2);
                Protocolo prot30 = new Protocolo(null, secEducacao, mun2, end3, "Centro dia idosos",
                                new Date(124, 2, 23), "Manutenção do centro", Status.CIENCIA, 45000.0, "030-2025",
                                prazo3);

                // Adicionando mais protocolos com meses diferentes...
                Protocolo prot31 = new Protocolo(null, secEducacao, mun1, end2, "Transporte escolar",
                                new Date(124, 3, 2), "Manutenção de frota", Status.CIENCIA, 75000.0,
                                "031-2025", prazo1);
                Protocolo prot32 = new Protocolo(null, secSaude, mun1, end4, "Farmácia popular", new Date(124, 3, 7),
                                "Abastecimento de medicamentos", Status.CIENCIA, 30000.0, "032-2025", prazo2);
                Protocolo prot33 = new Protocolo(null, secMeioAmb, mun1, end2, "Fiscalização ambiental",
                                new Date(124, 3, 12), "Operação de fiscalização", Status.CIENCIA, 15000.0, "033-2025",
                                prazo3);
                Protocolo prot34 = new Protocolo(null, secEducacao, mun2, end3, "Construção de creche",
                                new Date(124, 3, 17), "Obra do PAC", Status.CIENCIA, 600000.0, "034-2025", prazo1);
                Protocolo prot35 = new Protocolo(null, secSaude, mun1, end1, "Irrigação comunitária",
                                new Date(124, 3, 22), "Sistema de irrigação", Status.CIENCIA, 45000.0, "035-2025",
                                prazo2);

                Protocolo prot36 = new Protocolo(null, secSaude, mun1, end4, "Cras móvel", new Date(124, 4, 1),
                                "Atendimento em áreas remotas", Status.CIENCIA, 35000.0, "036-2025", prazo3);
                Protocolo prot37 = new Protocolo(null, secEducacao, mun2, end3, "Concurso público", new Date(124, 4, 6),
                                "Processo seletivo professores", Status.CIENCIA, 120000.0, "037-2025",
                                prazo1);
                Protocolo prot38 = new Protocolo(null, secSaude, mun1, end2, "SAMU", new Date(124, 4, 11),
                                "Manutenção de ambulâncias", Status.CIENCIA, 180000.0, "038-2025", prazo2);
                Protocolo prot39 = new Protocolo(null, secMeioAmb, mun1, end4, "Parque ecológico", new Date(124, 4, 16),
                                "Manutenção do parque", Status.CIENCIA, 25000.0, "039-2025", prazo3);
                Protocolo prot40 = new Protocolo(null, secEducacao, mun1, end1, "Casa popular", new Date(124, 4, 21),
                                "Construção de moradias", Status.CIENCIA, 800000.0, "040-2025", prazo1);

                // Continuando até 100...
                Protocolo prot41 = new Protocolo(null, secSaude, mun2, end3, "Feira agroecológica",
                                new Date(124, 5, 1), "Organização mensal", Status.CIENCIA, 8000.0, "041-2025", prazo2);
                Protocolo prot42 = new Protocolo(null, secSaude, mun1, end2, "Cadastro único",
                                new Date(124, 5, 5), "Atualização cadastral", Status.CIENCIA, 15000.0, "042-2025",
                                prazo3);
                Protocolo prot43 = new Protocolo(null, secEducacao, mun1, end4, "Biblioteca móvel",
                                new Date(124, 5, 10), "Projeto de leitura", Status.CIENCIA, 25000.0,
                                "043-2025", prazo1);
                Protocolo prot44 = new Protocolo(null, secSaude, mun2, end3, "Testes rápidos", new Date(124, 5, 15),
                                "Aquisição de kits", Status.CIENCIA, 30000.0, "044-2025", prazo2);
                Protocolo prot45 = new Protocolo(null, secMeioAmb, mun1, end2, "Reciclagem de óleo",
                                new Date(124, 5, 20), "Campanha de coleta", Status.CIENCIA, 5000.0, "045-2025",
                                prazo3);

                Protocolo prot46 = new Protocolo(null, secEducacao, mun1, end4, "Calçamento", new Date(124, 6, 1),
                                "Pavimentação de vias", Status.CIENCIA, 200000.0, "046-2025", prazo1);
                Protocolo prot47 = new Protocolo(null, secSaude, mun1, end1, "Defesa sanitária",
                                new Date(124, 6, 5), "Controle de pragas", Status.CIENCIA, 18000.0, "047-2025", prazo2);
                Protocolo prot48 = new Protocolo(null, secSaude, mun2, end3, "Acolhimento institucional",
                                new Date(124, 6, 10), "Casa de passagem", Status.CIENCIA, 60000.0, "048-2025",
                                prazo3);
                Protocolo prot49 = new Protocolo(null, secEducacao, mun1, end2, "Material escolar",
                                new Date(124, 6, 15), "Distribuição de kits", Status.CIENCIA, 120000.0,
                                "049-2025", prazo1);
                Protocolo prot50 = new Protocolo(null, secSaude, mun1, end4, "Oftalmologia", new Date(124, 6, 20),
                                "Consultas especializadas", Status.CIENCIA, 45000.0, "050-2025", prazo2);

                // Segunda metade...
                Protocolo prot51 = new Protocolo(null, secMeioAmb, mun2, end3, "Arborização", new Date(124, 7, 1),
                                "Plantio de mudas", Status.CIENCIA, 15000.0, "051-2025", prazo3);
                Protocolo prot52 = new Protocolo(null, secEducacao, mun1, end1, "Ponte estaiada", new Date(124, 7, 5),
                                "Construção de ponte", Status.CIENCIA, 1200000.0, "052-2025", prazo1);
                Protocolo prot53 = new Protocolo(null, secSaude, mun1, end4, "Aquicultura", new Date(124, 7, 10),
                                "Apoio a piscicultores", Status.CIENCIA, 30000.0, "053-2025", prazo2);
                Protocolo prot54 = new Protocolo(null, secSaude, mun1, end2, "Projeto jovem cidadão",
                                new Date(124, 7, 15), "Inclusão produtiva", Status.CIENCIA, 50000.0, "054-2025",
                                prazo3);
                Protocolo prot55 = new Protocolo(null, secEducacao, mun2, end3, "Robótica educacional",
                                new Date(124, 7, 20), "Oficinas nas escolas", Status.CIENCIA, 65000.0,
                                "055-2025", prazo1);

                Protocolo prot56 = new Protocolo(null, secSaude, mun1, end2, "CAPS", new Date(124, 8, 1),
                                "Manutenção do centro", Status.CIENCIA, 180000.0, "056-2025", prazo2);
                Protocolo prot57 = new Protocolo(null, secMeioAmb, mun1, end4, "Unidade conservação",
                                new Date(124, 8, 5), "Criação de reserva", Status.CIENCIA, 75000.0, "057-2025",
                                prazo3);
                Protocolo prot58 = new Protocolo(null, secEducacao, mun2, end3, "Saneamento básico",
                                new Date(124, 8, 10),
                                "Rede de esgoto", Status.CIENCIA, 900000.0, "058-2025", prazo1);
                Protocolo prot59 = new Protocolo(null, secSaude, mun1, end1, "Agricultura familiar",
                                new Date(124, 8, 15), "Fomento à produção", Status.CIENCIA, 40000.0, "059-2025",
                                prazo2);
                Protocolo prot60 = new Protocolo(null, secSaude, mun1, end4, "Cozinha comunitária",
                                new Date(124, 8, 20), "Refeições para carentes", Status.CIENCIA, 35000.0,
                                "060-2025", prazo3);

                // Continuando...
                Protocolo prot61 = new Protocolo(null, secEducacao, mun1, end2, "Educação inclusiva",
                                new Date(124, 9, 1), "Adaptação de escolas", Status.CIENCIA, 95000.0,
                                "061-2025", prazo1);
                Protocolo prot62 = new Protocolo(null, secSaude, mun2, end3, "Saúde bucal", new Date(124, 9, 5),
                                "Atendimento odontológico", Status.CIENCIA, 55000.0, "062-2025", prazo2);
                Protocolo prot63 = new Protocolo(null, secMeioAmb, mun1, end2, "Coleta de lixo eletrônico",
                                new Date(124, 9, 10), "Campanha de descarte", Status.CIENCIA, 12000.0, "063-2025",
                                prazo3);
                Protocolo prot64 = new Protocolo(null, secEducacao, mun1, end4, "Praça pública", new Date(124, 9, 15),
                                "Reforma da praça central", Status.CIENCIA, 180000.0, "064-2025", prazo1);
                Protocolo prot65 = new Protocolo(null, secSaude, mun2, end3, "Secagem de grãos",
                                new Date(124, 9, 20), "Equipamentos para secagem", Status.CIENCIA, 70000.0, "065-2025",
                                prazo2);

                Protocolo prot66 = new Protocolo(null, secSaude, mun1, end2, "Abrigo para mulheres",
                                new Date(124, 10, 1), "Casa de acolhimento", Status.CIENCIA, 120000.0, "066-2025",
                                prazo3);
                Protocolo prot67 = new Protocolo(null, secEducacao, mun1, end4, "Laboratório de ciências",
                                new Date(124, 10, 5), "Equipamentos para escolas", Status.CIENCIA, 85000.0,
                                "067-2025", prazo1);
                Protocolo prot68 = new Protocolo(null, secSaude, mun2, end3, "Fisioterapia", new Date(124, 10, 10),
                                "Aquisição de aparelhos", Status.CIENCIA, 65000.0, "068-2025", prazo2);
                Protocolo prot69 = new Protocolo(null, secMeioAmb, mun1, end2, "Educação ambiental",
                                new Date(124, 10, 15), "Projeto nas escolas", Status.CIENCIA, 15000.0, "069-2025",
                                prazo3);
                Protocolo prot70 = new Protocolo(null, secEducacao, mun1, end4, "Galerias pluviais",
                                new Date(124, 10, 20),
                                "Obras de drenagem", Status.CIENCIA, 350000.0, "070-2025", prazo1);

                // Últimos 30 protocolos...
                Protocolo prot71 = new Protocolo(null, secSaude, mun2, end3, "Silo comunitário",
                                new Date(124, 11, 1), "Armazenagem de grãos", Status.CIENCIA, 90000.0, "071-2025",
                                prazo2);
                Protocolo prot72 = new Protocolo(null, secSaude, mun1, end2, "Vale gás", new Date(124, 11, 5),
                                "Auxílio para famílias", Status.CIENCIA, 60000.0, "072-2025", prazo3);
                Protocolo prot73 = new Protocolo(null, secEducacao, mun1, end4, "Formação de professores",
                                new Date(124, 11, 10), "Capacitação continuada", Status.CIENCIA, 45000.0,
                                "073-2025", prazo1);
                Protocolo prot74 = new Protocolo(null, secSaude, mun2, end3, "UTI móvel", new Date(124, 11, 15),
                                "Aquisição de ambulância UTI", Status.CIENCIA, 500000.0, "074-2025", prazo2);
                Protocolo prot75 = new Protocolo(null, secMeioAmb, mun1, end2, "Monitoramento qualidade água",
                                new Date(124, 11, 20), "Análise de corpos hídricos", Status.CIENCIA, 35000.0,
                                "075-2025", prazo3);

                Protocolo prot76 = new Protocolo(null, secEducacao, mun1, end4, "Terminal rodoviário",
                                new Date(125, 0, 2),
                                "Construção de terminal", Status.CIENCIA, 1500000.0, "076-2026", prazo1);
                Protocolo prot77 = new Protocolo(null, secSaude, mun2, end3, "Irrigação por gotejamento",
                                new Date(125, 0, 7), "Sistemas de irrigação", Status.CIENCIA, 60000.0, "077-2026",
                                prazo2);
                Protocolo prot78 = new Protocolo(null, secSaude, mun1, end2, "Cras 24h", new Date(125, 0, 12),
                                "Ampliação de atendimento", Status.CIENCIA, 180000.0, "078-2026", prazo3);
                Protocolo prot79 = new Protocolo(null, secEducacao, mun1, end4, "Educação digital",
                                new Date(125, 0, 17), "Inclusão digital nas escolas", Status.CIENCIA,
                                120000.0, "079-2026", prazo1);
                Protocolo prot80 = new Protocolo(null, secSaude, mun2, end3, "Hemocentro", new Date(125, 0, 22),
                                "Ampliação de hemocentro", Status.CIENCIA, 800000.0, "080-2026", prazo2);

                Protocolo prot81 = new Protocolo(null, secMeioAmb, mun1, end2, "Recuperação de APP",
                                new Date(125, 1, 1), "Área de preservação", Status.CIENCIA, 45000.0, "081-2026",
                                prazo3);
                Protocolo prot82 = new Protocolo(null, secEducacao, mun1, end4, "Ciclovia", new Date(125, 1, 5),
                                "Construção de ciclovias", Status.CIENCIA, 250000.0, "082-2026", prazo1);
                Protocolo prot83 = new Protocolo(null, secSaude, mun2, end3, "Agroindústria",
                                new Date(125, 1, 10), "Apoio a pequenas agroindústrias", Status.CIENCIA, 95000.0,
                                "083-2026", prazo2);
                Protocolo prot84 = new Protocolo(null, secSaude, mun1, end2, "Moradia temporária",
                                new Date(125, 1, 15), "Abrigo emergencial", Status.CIENCIA, 200000.0, "084-2026",
                                prazo3);
                Protocolo prot85 = new Protocolo(null, secEducacao, mun1, end4, "Biblioteca digital",
                                new Date(125, 1, 20), "Plataforma de livros digitais", Status.CIENCIA,
                                75000.0, "085-2026", prazo1);

                Protocolo prot86 = new Protocolo(null, secSaude, mun2, end3, "Centro de reabilitação",
                                new Date(125, 2, 1), "Construção de centro", Status.CIENCIA, 1200000.0, "086-2026",
                                prazo2);
                Protocolo prot87 = new Protocolo(null, secMeioAmb, mun1, end2, "Coleta de pilhas", new Date(125, 2, 5),
                                "Pontos de coleta", Status.CIENCIA, 10000.0, "087-2026", prazo3);
                Protocolo prot88 = new Protocolo(null, secEducacao, mun1, end4, "Pavimentação asfáltica",
                                new Date(125, 2, 10), "Asfaltamento de vias", Status.CIENCIA, 600000.0, "088-2026",
                                prazo1);
                Protocolo prot89 = new Protocolo(null, secSaude, mun2, end3, "Fertilizantes orgânicos",
                                new Date(125, 2, 15), "Produção de compostagem", Status.CIENCIA, 35000.0, "089-2026",
                                prazo2);
                Protocolo prot90 = new Protocolo(null, secSaude, mun1, end2, "Centro convivência idosos",
                                new Date(125, 2, 20), "Atividades para idosos", Status.CIENCIA, 90000.0,
                                "090-2026", prazo3);

                Protocolo prot91 = new Protocolo(null, secEducacao, mun1, end4, "Educação infantil",
                                new Date(125, 3, 1), "Ampliação de vagas", Status.CIENCIA, 180000.0,
                                "091-2026", prazo1);
                Protocolo prot92 = new Protocolo(null, secSaude, mun2, end3, "Pronto atendimento", new Date(125, 3, 5),
                                "Ampliação de UPA", Status.CIENCIA, 1500000.0, "092-2026", prazo2);
                Protocolo prot93 = new Protocolo(null, secMeioAmb, mun1, end2, "Corredor ecológico",
                                new Date(125, 3, 10), "Conectividade de fragmentos", Status.CIENCIA, 80000.0,
                                "093-2026", prazo3);
                Protocolo prot94 = new Protocolo(null, secEducacao, mun1, end4, "Sistema de esgoto",
                                new Date(125, 3, 15),
                                "Ampliação de rede", Status.CIENCIA, 2000000.0, "094-2026", prazo1);
                Protocolo prot95 = new Protocolo(null, secSaude, mun2, end3, "Sementes crioulas",
                                new Date(125, 3, 20), "Banco de sementes", Status.CIENCIA, 25000.0, "095-2026", prazo2);

                Protocolo prot96 = new Protocolo(null, secSaude, mun1, end2, "Auxílio emergencial",
                                new Date(125, 4, 1), "Apoio a famílias em vulnerabilidade", Status.CIENCIA,
                                300000.0, "096-2026", prazo3);
                Protocolo prot97 = new Protocolo(null, secEducacao, mun1, end4, "Educação especial",
                                new Date(125, 4, 5), "Sala de recursos multifuncionais", Status.CIENCIA,
                                110000.0, "097-2026", prazo1);
                Protocolo prot98 = new Protocolo(null, secSaude, mun2, end3, "Sala de vacina", new Date(125, 4, 10),
                                "Reforma de salas", Status.CIENCIA, 75000.0, "098-2026", prazo2);
                Protocolo prot99 = new Protocolo(null, secMeioAmb, mun1, end2, "Unidade de conservação",
                                new Date(125, 4, 15), "Criação de parque municipal", Status.CIENCIA, 150000.0,
                                "099-2026", prazo3);
                Protocolo prot100 = new Protocolo(null, secEducacao, mun1, end4, "Urbanização de favela",
                                new Date(125, 4, 20), "Infraestrutura básica", Status.CIENCIA, 2500000.0,
                                "100-2026", prazo1);

                // Primeiros 6 protocolos (como no exemplo)
                prot1.setPrioridade(Prioridade.MEDIA);
                prot2.setPrioridade(Prioridade.BAIXA);
                prot3.setPrioridade(Prioridade.ALTA);
                prot4.setPrioridade(Prioridade.MEDIA);
                prot5.setPrioridade(Prioridade.BAIXA);
                prot6.setPrioridade(Prioridade.ALTA);

                // Protocolos 7-30
                prot7.setPrioridade(Prioridade.ALTA);
                prot8.setPrioridade(Prioridade.MEDIA);
                prot9.setPrioridade(Prioridade.BAIXA);
                prot10.setPrioridade(Prioridade.ALTA);
                prot11.setPrioridade(Prioridade.MEDIA);
                prot12.setPrioridade(Prioridade.BAIXA);
                prot13.setPrioridade(Prioridade.ALTA);
                prot14.setPrioridade(Prioridade.MEDIA);
                prot15.setPrioridade(Prioridade.BAIXA);
                prot16.setPrioridade(Prioridade.ALTA);
                prot17.setPrioridade(Prioridade.MEDIA);
                prot18.setPrioridade(Prioridade.BAIXA);
                prot19.setPrioridade(Prioridade.ALTA);
                prot20.setPrioridade(Prioridade.MEDIA);
                prot21.setPrioridade(Prioridade.BAIXA);
                prot22.setPrioridade(Prioridade.ALTA);
                prot23.setPrioridade(Prioridade.MEDIA);
                prot24.setPrioridade(Prioridade.BAIXA);
                prot25.setPrioridade(Prioridade.ALTA);

                // Protocolos 26-50
                prot26.setPrioridade(Prioridade.MEDIA);
                prot27.setPrioridade(Prioridade.BAIXA);
                prot28.setPrioridade(Prioridade.ALTA);
                prot29.setPrioridade(Prioridade.MEDIA);
                prot30.setPrioridade(Prioridade.BAIXA);
                prot31.setPrioridade(Prioridade.ALTA);
                prot32.setPrioridade(Prioridade.MEDIA);
                prot33.setPrioridade(Prioridade.BAIXA);
                prot34.setPrioridade(Prioridade.ALTA);
                prot35.setPrioridade(Prioridade.MEDIA);
                prot36.setPrioridade(Prioridade.BAIXA);
                prot37.setPrioridade(Prioridade.ALTA);
                prot38.setPrioridade(Prioridade.MEDIA);
                prot39.setPrioridade(Prioridade.BAIXA);
                prot40.setPrioridade(Prioridade.ALTA);
                prot41.setPrioridade(Prioridade.MEDIA);
                prot42.setPrioridade(Prioridade.BAIXA);
                prot43.setPrioridade(Prioridade.ALTA);
                prot44.setPrioridade(Prioridade.MEDIA);
                prot45.setPrioridade(Prioridade.BAIXA);
                prot46.setPrioridade(Prioridade.ALTA);
                prot47.setPrioridade(Prioridade.MEDIA);
                prot48.setPrioridade(Prioridade.BAIXA);
                prot49.setPrioridade(Prioridade.ALTA);
                prot50.setPrioridade(Prioridade.MEDIA);

                // Protocolos 51-75
                prot51.setPrioridade(Prioridade.BAIXA);
                prot52.setPrioridade(Prioridade.ALTA);
                prot53.setPrioridade(Prioridade.MEDIA);
                prot54.setPrioridade(Prioridade.BAIXA);
                prot55.setPrioridade(Prioridade.ALTA);
                prot56.setPrioridade(Prioridade.MEDIA);
                prot57.setPrioridade(Prioridade.BAIXA);
                prot58.setPrioridade(Prioridade.ALTA);
                prot59.setPrioridade(Prioridade.MEDIA);
                prot60.setPrioridade(Prioridade.BAIXA);
                prot61.setPrioridade(Prioridade.ALTA);
                prot62.setPrioridade(Prioridade.MEDIA);
                prot63.setPrioridade(Prioridade.BAIXA);
                prot64.setPrioridade(Prioridade.ALTA);
                prot65.setPrioridade(Prioridade.MEDIA);
                prot66.setPrioridade(Prioridade.BAIXA);
                prot67.setPrioridade(Prioridade.ALTA);
                prot68.setPrioridade(Prioridade.MEDIA);
                prot69.setPrioridade(Prioridade.BAIXA);
                prot70.setPrioridade(Prioridade.ALTA);
                prot71.setPrioridade(Prioridade.MEDIA);
                prot72.setPrioridade(Prioridade.BAIXA);
                prot73.setPrioridade(Prioridade.ALTA);
                prot74.setPrioridade(Prioridade.MEDIA);
                prot75.setPrioridade(Prioridade.BAIXA);

                // Protocolos 76-100
                prot76.setPrioridade(Prioridade.ALTA);
                prot77.setPrioridade(Prioridade.MEDIA);
                prot78.setPrioridade(Prioridade.BAIXA);
                prot79.setPrioridade(Prioridade.ALTA);
                prot80.setPrioridade(Prioridade.MEDIA);
                prot81.setPrioridade(Prioridade.BAIXA);
                prot82.setPrioridade(Prioridade.ALTA);
                prot83.setPrioridade(Prioridade.MEDIA);
                prot84.setPrioridade(Prioridade.BAIXA);
                prot85.setPrioridade(Prioridade.ALTA);
                prot86.setPrioridade(Prioridade.MEDIA);
                prot87.setPrioridade(Prioridade.BAIXA);
                prot88.setPrioridade(Prioridade.ALTA);
                prot89.setPrioridade(Prioridade.MEDIA);
                prot90.setPrioridade(Prioridade.BAIXA);
                prot91.setPrioridade(Prioridade.ALTA);
                prot92.setPrioridade(Prioridade.MEDIA);
                prot93.setPrioridade(Prioridade.BAIXA);
                prot94.setPrioridade(Prioridade.ALTA);
                prot95.setPrioridade(Prioridade.MEDIA);
                prot96.setPrioridade(Prioridade.BAIXA);
                prot97.setPrioridade(Prioridade.ALTA);
                prot98.setPrioridade(Prioridade.MEDIA);
                prot99.setPrioridade(Prioridade.BAIXA);
                prot100.setPrioridade(Prioridade.ALTA);

                prot1.setPrioridade(Prioridade.MEDIA);
                prot2.setPrioridade(Prioridade.BAIXA);
                prot3.setPrioridade(Prioridade.ALTA);
                prot4.setPrioridade(Prioridade.MEDIA);
                prot5.setPrioridade(Prioridade.BAIXA);
                prot6.setPrioridade(Prioridade.ALTA);

                Assunto assunto1 = new Assunto(1, "Problema de iluminação pública", secSaude, 130.00, Prioridade.MEDIA,
                                5);

                Assunto assunto2 = new Assunto(2, "Problema de coleta de lixo", secEducacao, 150.00, Prioridade.BAIXA,
                                7);

                Assunto assunto3 = new Assunto(3, "Problema de trânsito", secMeioAmb, 30.00, Prioridade.ALTA, 3);

                Assunto assunto4 = new Assunto(4, "Outros", secSaude, 30.00, Prioridade.BAIXA, 7);

                Devolutiva dev1 = new Devolutiva(null, null, prot1, Instant.now(), "Teste");
                // Manda para o banco de dados
                municipeRepository.saveAll(Arrays.asList(mun1, mun2, secretario));
                enderecoRepository.saveAll(Arrays.asList(end1, end2, end3, end6));
                secretariaRepository.saveAll(Arrays.asList(secSaude, secEducacao, secMeioAmb));

                // Salvando os protocolos no banco de dados em lotes
                protocoloRepository.saveAll(
                                Arrays.asList(prot1, prot2, prot3, prot4, prot5, prot6, prot7, prot8, prot9, prot10));
                protocoloRepository.saveAll(Arrays.asList(prot11, prot12, prot13, prot14, prot15, prot16, prot17,
                                prot18, prot19, prot20));
                protocoloRepository.saveAll(Arrays.asList(prot21, prot22, prot23, prot24, prot25, prot26, prot27,
                                prot28, prot29, prot30));
                protocoloRepository.saveAll(Arrays.asList(prot31, prot32, prot33, prot34, prot35, prot36, prot37,
                                prot38, prot39, prot40));
                protocoloRepository.saveAll(Arrays.asList(prot41, prot42, prot43, prot44, prot45, prot46, prot47,
                                prot48, prot49, prot50));
                protocoloRepository.saveAll(Arrays.asList(prot51, prot52, prot53, prot54, prot55, prot56, prot57,
                                prot58, prot59, prot60));
                protocoloRepository.saveAll(Arrays.asList(prot61, prot62, prot63, prot64, prot65, prot66, prot67,
                                prot68, prot69, prot70));
                protocoloRepository.saveAll(Arrays.asList(prot71, prot72, prot73, prot74, prot75, prot76, prot77,
                                prot78, prot79, prot80));
                protocoloRepository.saveAll(Arrays.asList(prot81, prot82, prot83, prot84, prot85, prot86, prot87,
                                prot88, prot89, prot90));
                protocoloRepository.saveAll(Arrays.asList(prot91, prot92, prot93, prot94, prot95, prot96, prot97,
                                prot98, prot99, prot100));

                assuntoRepository.saveAll((Arrays.asList(assunto1, assunto2, assunto3, assunto4)));
                devolutivaRepository.save(dev1);
        }
}
