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

                Secretaria secEducacao = new Secretaria(null, "Secretaria de Educação", "Ana Silva", "ana@email.com"
                , end2);

                Secretaria secSaude = new Secretaria(null, "Secretaria de Saúde", "Carlos Santos", "carlos@email.com"
                                , end3);

                Secretaria secMeioAmb = new Secretaria(null, "Secretaria de Meio Ambiente", "Mariana Oliveira",
                                "mariana@email.com"
                                , end1);

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

                protocoloRepository.saveAll(Arrays.asList(prot1, prot2, prot3, prot4, prot5, prot6));
                assuntoRepository.saveAll((Arrays.asList(assunto1, assunto2, assunto3, assunto4)));
                devolutivaRepository.save(dev1);
        }
}
