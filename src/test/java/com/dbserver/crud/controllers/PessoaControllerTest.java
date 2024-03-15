package com.dbserver.crud.controllers;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import com.dbserver.crud.controllers.utils.TesteUtils;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import com.dbserver.crud.infra.excecao.RespostaErro;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PessoaControllerTest {

    @Autowired
    private JacksonTester<CriarPessoaDto> criarPessoaJson;

    @Autowired
    private JacksonTester<AtualizarDadosPessoaDto> atualizarDadosPessoaJson;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MockMvc mockMvc;

    private Pessoa pessoaLogada;

    private void popularBanco() {
        Pessoa pessoa1 = new Pessoa(1L, "pessoaNova", "123456", "Pedro", LocalDate.of(2000, 12, 15), "11111555551",
                this.passwordEncoder);
        Pessoa pessoa2 = new Pessoa(2L, "outraPessoa", "654321", "Maria", LocalDate.of(1995, 5, 20), "22222666662",
                this.passwordEncoder);

        Pessoa pessoa3 = new Pessoa(3L, "maisUmaPessoa", "abcdef", "João", LocalDate.of(1980, 8, 10), "33333777773",
                this.passwordEncoder);

        Pessoa pessoa4 = new Pessoa(4L, "novaPessoa", "987654", "Ana", LocalDate.of(1988, 3, 25), "44444888884",
                this.passwordEncoder);
        var pessoas = this.pessoaRepository.saveAllAndFlush(List.of(pessoa1, pessoa2, pessoa3, pessoa4));

        this.pessoaLogada = pessoas.get(0);
    }

    

    @BeforeEach
    void setUp() {
        this.popularBanco();
        TesteUtils.login(this.pessoaLogada);
    }

    @AfterEach
    void tearDown() {
        this.pessoaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar Status 201 ao criar pessoa sem endereço")
    void deveCriarPessoaSemEndereco() throws Exception {
        CriarPessoaDto novaPessoa = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                "12345678911", null);

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/pessoa/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarPessoaJson.write(novaPessoa).getJson()))
                .andReturn().getResponse();

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    }

    @Test
    @DisplayName("Deve retornar Status 201 ao criar pessoa com endereço")
    void deveCriarPessoaComEndereco() throws Exception {
        CriarEnderecoDto novoEndereco = new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu", "Belo Horizonte",
                "Minas Gerais", "31872140", true);
        CriarPessoaDto novaPessoa = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                "12345678911", List.of(novoEndereco));

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/pessoa/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarPessoaJson.write(novaPessoa).getJson()))
                .andReturn().getResponse();
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaCriarPessoa() {
        return Stream.of(
                Arguments.of("tem", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Login deve ter no mínimo 5 caracteres"),
                Arguments.of("temtr", "12345", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Senha deve conter no mínimo 6 caracteres"),
                Arguments.of("temtr", "123456", "Pe", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Nome deve ter no mínimo 3 caracteres"),
                Arguments.of("temtr", "123456", "Pedro", null, "12345678910", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Data de nascimento deve ser informada"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "1234567891", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Cpf deve conter 11 caracteres numéricos"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", " ",
                        "123", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Rua deve ser informada"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        null, "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Número deve ser informado"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "ad12", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Número deve ter somente caracteres numéricos"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", " ", "Belo Horizonte", "MG",
                        "12345678", "Bairro deve ser informado"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", "Bairro Central", " ", "MG",
                        "12345678", "Cidade deve ser informada"),
                Arguments.of("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910", "Rua das Flores",
                        "123", "Bairro Central", "Belo Horizonte", " ",
                        "12345678", "Estado deve ser informado"));
    }

    @DisplayName("Deve retornar Status 400 ao criar pessoa com dados inválidos")
    @MethodSource("argumentosDadosInvalidosParaCriarPessoa")
    @ParameterizedTest
    void deveFalharAoCriarPessoaComDadosInvalidos(String login, String senha, String nome, LocalDate dataNascimento,
            String cpf, String rua, String numero, String bairro, String cidade, String estado, String cep,
            String mensagem) throws Exception {
        CriarEnderecoDto novoEndereco = new CriarEnderecoDto(rua, numero, bairro, cidade, estado, cep, true);

        CriarPessoaDto novaPessoa = new CriarPessoaDto(login, senha, nome, dataNascimento, cpf, List.of(novoEndereco));

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/pessoa/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarPessoaJson.write(novaPessoa).getJson()))
                .andReturn().getResponse();
        System.out.println(resposta.getContentAsString());
        RespostaErro respostaErro = this.objectMapper.readValue(resposta.getContentAsString(StandardCharsets.UTF_8),
                RespostaErro.class);

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertEquals(mensagem, respostaErro.getErro());
    }

    @Test
    @DisplayName("Deve retornar Status 200 ao atualizar usuário logado")
    void deveAtualizarPessoa() throws Exception {

        AtualizarDadosPessoaDto novasDados = new AtualizarDadosPessoaDto("Pedro Samuel", "12345678",
                LocalDate.of(2005, 12, 15),
                "12345678911");

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .put("/pessoa/atualizar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atualizarDadosPessoaJson.write(novasDados).getJson()))
                .andReturn().getResponse();

        System.out.println(resposta.getContentAsString());
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve retornar Status 200 ao atualizar somente um campo")
    void deveAtualizarSomenteUmCampoDePessoa() throws Exception {

        AtualizarDadosPessoaDto novasDados = new AtualizarDadosPessoaDto("Pedro Samuel", null,
                null,
                null);

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .put("/pessoa/atualizar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atualizarDadosPessoaJson.write(novasDados).getJson()))
                .andReturn().getResponse();

        System.out.println(resposta.getContentAsString());
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaAtualizar() {
        return Stream.of(
                Arguments.of(null, "123", null),
                Arguments.of(null, null, "12345678"),
                Arguments.of("eu", null, null));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaAtualizar")
    @DisplayName("Deve falhar ao tentar atualizar pessoa com dados inválidos: senha = {0}, cpf = {1}, nome = {2}")
    void deveFalharAoAtualizarComDadosInvalidos(String nome, String senha, String cpf) throws Exception {

        AtualizarDadosPessoaDto novasDados = new AtualizarDadosPessoaDto(nome, senha,
                null,
                cpf);

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .put("/pessoa/atualizar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(atualizarDadosPessoaJson.write(novasDados).getJson()))
                .andReturn().getResponse();

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deve ser possível buscar todas pessoas")
    void deveBuscarTodasPessoas() throws Exception {
        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .get("/pessoa")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deve ser possível buscar todas pessoas utilizando paginação")
    void deveBuscarTodasPessoasComPaginacao() throws Exception {
        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .get("/pessoa?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        List<PessoaRespostaDto> listaResposta = objectMapper.readValue(
                resposta.getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<PessoaRespostaDto>>() {
                });
        assertEquals(2, listaResposta.size());
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    @DisplayName("Deve ser possível a pessoa logada deletar sua própria conta")
    void deveDeletarAContaLogada() throws Exception {

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .delete("/pessoa/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

}
