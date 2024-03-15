package com.dbserver.crud.domain.autenticacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Optional;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.infra.security.TokenService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEnconder;

    @Mock
    private Autenticacao autenticacao;

    @Mock
    private Pessoa pessoaMock;

    @BeforeEach
    void setup() {
        this.autenticacao = new Autenticacao("usuario123", "senha123");
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15),
                                "12345678910",
                                this.passwordEnconder);
    }
    @Test
    @DisplayName("Deve ser possível se autenticar ao passar dados válidos")
    void deveSerPossivelSeAutenticarAoPassarDadosValidos() {
        when(pessoaRepository.findByLogin(this.autenticacao.getLogin())).thenReturn(Optional.of(this.pessoaMock));
        when(this.passwordEnconder.matches(any(), any())).thenReturn(true);
        when(this.tokenService.gerarToken(this.pessoaMock)).thenReturn("tokenGerado");
        String token = this.autenticacaoService.autenticar(this.autenticacao);
        assertEquals("tokenGerado", token);
    }
    @Test
    @DisplayName("Deve falhar ao enviar senha incorreta  ao se autenticar")
    void deveFalharAoPassarSenhaIncorreta() {
        when(pessoaRepository.findByLogin(this.autenticacao.getLogin())).thenReturn(Optional.of(this.pessoaMock));
        when(this.passwordEnconder.matches(any(), any())).thenReturn(false);
        assertThrows(BadCredentialsException.class, ()->this.autenticacaoService.autenticar(autenticacao));
    }
    @Test
    @DisplayName("Deve falhar ao enviar login incorreto ao se autenticar")
    void deveFalharAoPassarLoginIncorreto() {
        when(pessoaRepository.findByLogin(this.autenticacao.getLogin())).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, ()->this.autenticacaoService.autenticar(autenticacao));
    }
}
