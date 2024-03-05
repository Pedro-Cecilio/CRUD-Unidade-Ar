package com.dbserver.crud.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;


import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class PessoaControllerTest {

    @Autowired
    private JacksonTester<CriarPessoaDTO> criarPessoaJson;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost";
        port = 8080;
    }

    @Test
    void givenUsuarioWhenCriarPessoaThenRetornarStatus201() throws IOException {
        CriarPessoaDTO novaPessoa = new CriarPessoaDTO("Pedro", LocalDate.of(2000, 12, 15), "12345678911");
        given()
                .body(criarPessoaJson.write(novaPessoa).getJson())
                .contentType("application/json")
                .when()
                .post("/pessoa")
                .then()
                .log().all()
                .statusCode(201);

    }
}
