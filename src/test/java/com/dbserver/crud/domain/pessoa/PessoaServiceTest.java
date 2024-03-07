package com.dbserver.crud.domain.pessoa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import com.dbserver.crud.infra.security.TokenService;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {
    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve ser possível criar uma pessoa ao passar dados corretamente")
    void deveCriarPessoaPassandoTodosDados() {
        List<CriarEnderecoDto> enderecos = List.of(new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true));
        CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910",
                enderecos);

        PessoaRespostaDto resposta = this.pessoaService.criarPessoa(dto);

        assertEquals(dto.nome(), resposta.nome());
        assertEquals(dto.login(), resposta.login());
        assertEquals(dto.dataNascimento().toString(), resposta.dataNascimento().toString());
        assertEquals(dto.cpf(), resposta.cpf());
        assertEquals(dto.enderecos().size(), resposta.enderecos().size());
    }

    @Test
    @DisplayName("Deve ser possível criar uma pessoa ao passar dados corretamente, sem passar endereço")
    void deveCriarPessoaSemEndereco() {
        CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "12345678910",
                List.of());

        PessoaRespostaDto resposta = this.pessoaService.criarPessoa(dto);

        assertEquals(dto.nome(), resposta.nome());
        assertEquals(dto.login(), resposta.login());
        assertEquals(dto.dataNascimento().toString(), resposta.dataNascimento().toString());
        assertEquals(dto.cpf(), resposta.cpf());
        assertEquals(dto.enderecos().size(), resposta.enderecos().size());
    }

    @Test
    @DisplayName("Não Deve ser possível criar uma pessoa com senha menor que 6 caracteres")
    void deveFalharAoCriarPessoaComSenhaInvalida() {
        CriarPessoaDto dto = new CriarPessoaDto("temtr", "12345", "Pedro", LocalDate.of(2000, 12, 15), "12345678910",
                List.of());

        assertThrows(IllegalArgumentException.class, () -> this.pessoaService.criarPessoa(dto));
    }
    @Test
    @DisplayName("Não Deve ser possível criar uma pessoa com cpf sem ter exatos 11 caracteres")
    void deveFalharAoCriarPessoaComCpfInvalido() {
        CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15), "1234567891",
                List.of());

        assertThrows(IllegalArgumentException.class, () -> this.pessoaService.criarPessoa(dto));
    }
    @Test
    @DisplayName("Não Deve ser possível criar uma pessoa com login nulo")
    void deveFalharAoCriarPessoaComLoginNulo() {
        CriarPessoaDto dto = new CriarPessoaDto(null, "123456", "Pedro", LocalDate.of(2000, 12, 15), "1234567891",
                List.of());

        assertThrows(IllegalArgumentException.class, () -> this.pessoaService.criarPessoa(dto));
    }
    @Test
    @DisplayName("Não Deve ser possível criar uma pessoa com data nula")
    void deveFalharAoCriarPessoaComDataNula() {
        CriarPessoaDto dto = new CriarPessoaDto(null, "123456", "Pedro", null, "1234567891",
                List.of());

        assertThrows(IllegalArgumentException.class, () -> this.pessoaService.criarPessoa(dto));
    }

}
