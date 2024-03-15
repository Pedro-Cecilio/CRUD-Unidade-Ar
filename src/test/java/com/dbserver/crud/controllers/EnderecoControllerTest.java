package com.dbserver.crud.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.json.JacksonTester;
import com.dbserver.crud.controllers.utils.TesteUtils;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.infra.excecao.RespostaErro;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Stream;
import java.util.List;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EnderecoControllerTest {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private PessoaRepository pessoaRepository;

        @Autowired
        private EnderecoRepository enderecoRepository;

        @Autowired
        private JacksonTester<CriarEnderecoDto> criarEnderecoDtoJson;
        @Autowired
        private JacksonTester<AtualizarEnderecoDto> atualizarEnderecoDtoJson;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private Pessoa pessoaLogada;
        private Pessoa pessoaSemEnderecoPrincipal;
        private Long idEnderecoPessoaLogada;
        private Long idEnderecoOutraPessoa;

        private AtualizarEnderecoDto atualizarEnderecoDtoTodosDados;

        private List<Pessoa> popularBanco() {
                Pessoa pessoa = new Pessoa(1L, "pessoaNova", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "11111555551",
                                this.passwordEncoder);

                Pessoa pessoa2 = new Pessoa(2L, "outraPessoa", "654321", "Maria", LocalDate.of(1995, 5, 20),
                                "22222666662", this.passwordEncoder);

                List<Pessoa> pessoasSalvas = this.pessoaRepository.saveAll(List.of(pessoa, pessoa2));

                Endereco endereco = new Endereco(1L, "Rua das Flores", "123", "Centro", "São Paulo", "SP",
                                "12345678", true, pessoasSalvas.get(0));
                Endereco endereco2 = new Endereco(2L, "Avenida Principal", "456", "Bairro Novo", "Rio de Janeiro", "RJ",
                                "98765432", false, pessoasSalvas.get(1));
                List<Endereco> enderecosSalvos = this.enderecoRepository.saveAll(List.of(endereco, endereco2));

                pessoasSalvas.get(0).setEndereco(enderecosSalvos.get(0));
                pessoasSalvas.get(1).setEndereco(enderecosSalvos.get(1));

                return this.pessoaRepository.saveAll(List.of(pessoasSalvas.get(0), pessoasSalvas.get(1)));

        }

        public void definirVariaveisRelacionadasAPessoa(List<Pessoa> pessoasSalvas) {
                this.idEnderecoPessoaLogada = pessoasSalvas.get(0).getEndereco().get(0).getId();
                this.idEnderecoOutraPessoa = pessoasSalvas.get(1).getEndereco().get(0).getId();
                this.pessoaLogada = pessoasSalvas.get(0);
                this.pessoaSemEnderecoPrincipal = pessoasSalvas.get(1);
        }

        @BeforeEach
        void setUp() {
                this.atualizarEnderecoDtoTodosDados = new AtualizarEnderecoDto("Rua das Pedras", "321", "Longe",
                                "Espirito Santo", "ES",
                                "87654321", true);
                List<Pessoa> pessoasSalvas = this.popularBanco();
                definirVariaveisRelacionadasAPessoa(pessoasSalvas);
                TesteUtils.login(this.pessoaLogada);
        }

        // @AfterEach
        // void tearDown() {
        // // this.enderecoRepository.deleteAll();
        // this.pessoaRepository.deleteAll();
        // }

        @Test
        @DisplayName("Deve ser possível cadastrar um endereço")
        void deveCadastrarUmEndereco() throws Exception {
                CriarEnderecoDto novoEndereco = new CriarEnderecoDto("Rua das Flores", "123", "Centro", "São Paulo",
                                "SP",
                                "12345678", false);
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .post("/endereco")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(criarEnderecoDtoJson.write(novoEndereco).getJson()))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        }

        private static Stream<Arguments> argumentosDadosInvalidosParaCriarEndereco() {
                return Stream.of(
                                Arguments.of(" ", "123", "Bairro Central", "Belo Horizonte", "MG",
                                                "12345678",
                                                "Rua deve ser informada"),
                                Arguments.of("Rua das Flores", "abc", "Bairro Central", "Belo Horizonte",
                                                "MG",
                                                "12345678", "Número deve ter somente caracteres numéricos"),
                                Arguments.of("Rua das Flores", "123", " ", "Belo Horizonte", "MG",
                                                "12345678",
                                                "Bairro deve ser informado"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", " ", "MG", "12345678",
                                                "Cidade deve ser informada"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", " ",
                                                "12345678", "Estado deve ser informado"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte",
                                                "MG",
                                                "31872", "Cep deve ter 8 caracteres numéricos"));
        }

        @ParameterizedTest
        @MethodSource("argumentosDadosInvalidosParaCriarEndereco")
        @DisplayName("Deve falhar ao cadastrar um endereço com dado inválido")
        void deveFalhaAoCadastrarUmEndereco(String rua, String numero, String bairro,
                        String cidade,
                        String estado, String cep, String mensagem) throws Exception {
                CriarEnderecoDto novoEndereco = new CriarEnderecoDto(rua, numero, bairro,
                                cidade, estado, cep, false);
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .post("/endereco")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(criarEnderecoDtoJson.write(novoEndereco).getJson()))
                                .andReturn().getResponse();
                RespostaErro respostaErro = this.objectMapper.readValue(
                                resposta.getContentAsString(StandardCharsets.UTF_8),
                                RespostaErro.class);

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertEquals(mensagem, respostaErro.getErro());
        }

        @Test
        @DisplayName("Deve ser possível atualizar um endereço")
        void deveAtualizarUmEndereco() throws Exception {

                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .put("/endereco/%d".formatted(this.idEnderecoPessoaLogada))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(atualizarEnderecoDtoJson.write(this.atualizarEnderecoDtoTodosDados).getJson()))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("Deve ser possível atualizar um endereço passando somente um campo")
        void deveAtualizarSomenteUmCampoDeUmEndereco() throws Exception {

                AtualizarEnderecoDto novosDados = new AtualizarEnderecoDto(null, null, null,
                                null, null,
                                null, true);
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .put("/endereco/%d".formatted(this.idEnderecoPessoaLogada))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(atualizarEnderecoDtoJson.write(novosDados).getJson()))
                                .andReturn().getResponse();
                System.out.println("------------- DADOS USUARIO LOGADO ----------------");
                System.out.println(this.pessoaLogada.getId());
                System.out.println(this.pessoaLogada.getEndereco().get(0));
                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("Não Deve ser possível atualizar um endereço de outra pessoa")
        void deveFalharAoTentarAtualizarUmEnderecoDeOutraPessoa() throws Exception {
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .put("/endereco/%d".formatted(this.idEnderecoOutraPessoa))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(atualizarEnderecoDtoJson.write(this.atualizarEnderecoDtoTodosDados).getJson()))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        private static Stream<Arguments> argumentosDadosInvalidosParaAtualizarEndereco() {
                return Stream.of(
                                Arguments.of(" ", "123", "Bairro Central", "Belo Horizonte", "MG",
                                                "12345678",
                                                "Rua deve ser informada"),
                                Arguments.of("Rua das Flores", "abc", "Bairro Central", "Belo Horizonte",
                                                "MG",
                                                "12345678", "Número deve ter somente caracteres numéricos"),
                                Arguments.of("Rua das Flores", "123", " ", "Belo Horizonte", "MG",
                                                "12345678",
                                                "Bairro deve ser informado"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", " ", "MG",
                                                "12345678",
                                                "Cidade deve ser informada"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", " ",
                                                "12345678", "Estado deve ser informado"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte",
                                                "MG",
                                                "31872", "Cep deve ter 8 caracteres numéricos"));
        }

        @ParameterizedTest
        @DisplayName("Deve falhar ao tentar atualizar com dados inválidos: rua = {1},numero = {2}, bairro = {3}, cidade = {4}, estado = {5}, cep = {6}")
        @MethodSource("argumentosDadosInvalidosParaAtualizarEndereco")
        void deveFalharAoAtualizarComDadosInvalidos(String rua, String numero, String bairro, String cidade,
                        String estado, String cep, String mensagem) throws Exception {
                AtualizarEnderecoDto novoEndereco = new AtualizarEnderecoDto(rua, numero,
                                bairro, cidade, estado, cep,
                                true);

                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .put("/endereco/%d".formatted(this.idEnderecoPessoaLogada))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(atualizarEnderecoDtoJson.write(novoEndereco).getJson()))
                                .andReturn().getResponse();

                RespostaErro respostaErro = this.objectMapper.readValue(
                                resposta.getContentAsString(StandardCharsets.UTF_8),
                                RespostaErro.class);

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertEquals(mensagem, respostaErro.getErro());

        }

        @Test
        @DisplayName("Deve ser possível buscar todos enderecos")
        void deveBuscarTodosEnderecos() throws Exception {

                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .get("/endereco")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("Deve ser possível buscar o endereco principal da conta logada se houver")
        void deveBuscarEnderecoPrincipal() throws Exception {
                pessoaLogada.getEndereco().size();

                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .get("/endereco/principal")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("Deve falhar ao buscar endereco principal da conta logada quando não houver")
        void deveFalharAoBuscarEnderecoPrincipal() throws Exception {
                TesteUtils.login(this.pessoaSemEnderecoPrincipal);
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .get("/endereco/principal")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("Deve ser possível deletar um endereço")
        void deveDeletarUmEndereco() throws Exception {
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .delete("/endereco/%d".formatted(this.idEnderecoPessoaLogada))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();
                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("Não deve ser possível deletar um endereço que não possui")
        void deveFalharDeletarUmEnderecoQueNaoPossui() throws Exception {
                MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                                .delete("/endereco/%d".formatted(this.idEnderecoOutraPessoa))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();
                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
                System.out.println(this.pessoaRepository.findAll().size());
        }

}
