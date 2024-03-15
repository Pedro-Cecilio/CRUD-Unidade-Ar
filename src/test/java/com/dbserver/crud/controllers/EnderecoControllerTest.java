package com.dbserver.crud.controllers;

import org.junit.jupiter.api.AfterEach;
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
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.infra.excecao.RespostaErro;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EnderecoControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private JacksonTester<CriarEnderecoDto> criarEnderecoDtoJson;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Pessoa pessoa = new Pessoa(1L, "pessoaNova", "123456", "Pedro", LocalDate.of(2000, 12, 15), "11111555551",
                this.passwordEncoder);
        Pessoa pessoaSalva = this.pessoaRepository.save(pessoa);
        TesteUtils.login(pessoaSalva);
    }

    @AfterEach
    void tearDown() {
        this.pessoaRepository.deleteAll();
        this.enderecoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve ser possível cadastrar um endereço")
    void deveCadastrarUmEndereco() throws Exception {
        CriarEnderecoDto novoEndereco = new CriarEnderecoDto("Rua das Flores", "123", "Centro", "São Paulo", "SP",
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
                Arguments.of(" ", "123", "Bairro Central", "Belo Horizonte", "MG", "12345678",
                        "Rua deve ser informada"),
                Arguments.of("Rua das Flores", "abc", "Bairro Central", "Belo Horizonte", "MG",
                        "12345678", "Número com formato inválido"),
                Arguments.of("Rua das Flores", "123", " ", "Belo Horizonte", "MG", "12345678",
                        "Bairro deve ser informado"),
                Arguments.of("Rua das Flores", "123", "Bairro Central", " ", "MG", "12345678",
                        "Cidade deve ser informada"),
                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", " ",
                        "12345678", "Estado deve ser informado"),
                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", "MG",
                        "31872", "Cep com formato inválido"));
    }
    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaCriarEndereco")
    @DisplayName("Deve falhar ao cadastrar um endereço com dado inválido")
    void deveFalhaAoCadastrarUmEndereco(String rua, String numero, String bairro, String cidade,
            String estado, String cep, String mensagem) throws Exception {
        CriarEnderecoDto novoEndereco = new CriarEnderecoDto(rua, numero, bairro, cidade, estado, cep, false);
        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/endereco")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarEnderecoDtoJson.write(novoEndereco).getJson()))
                .andReturn().getResponse();
        RespostaErro respostaErro = this.objectMapper.readValue(resposta.getContentAsString(StandardCharsets.UTF_8),
                RespostaErro.class);

        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertEquals(mensagem, respostaErro.getErro());
    }
}
