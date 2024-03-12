package com.dbserver.crud.controllers;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import java.time.LocalDate;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PessoaControllerTest {

    @Autowired
    private JacksonTester<CriarPessoaDto> criarPessoaJson;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach 
    void setUp() {
        this.pessoaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar Status 201 ao criar pessoa sem endereço")
    void deveCriarPessoaSemEndereco() throws Exception {
        CriarPessoaDto novaPessoa = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678911", null);

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/pessoa/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarPessoaJson.write(novaPessoa).getJson()))
                .andReturn().getResponse();
        System.out.println(resposta.getContentAsString());
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }
    @Test
    @DisplayName("Deve retornar Status 201 ao criar pessoa com endereço")
    @WithMockUser
    void deveCriarPessoaComEndereco() throws Exception {
        CriarEnderecoDto novoEndereco = new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu", "Belo Horizonte", "Minas Gerais", "31872140", true);
        CriarPessoaDto novaPessoa = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678911", List.of(novoEndereco));

        MockHttpServletResponse resposta = mockMvc.perform(MockMvcRequestBuilders
                .post("/pessoa/novo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarPessoaJson.write(novaPessoa).getJson()))
                .andReturn().getResponse();
        System.out.println(resposta.getContentAsString());
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }
}
