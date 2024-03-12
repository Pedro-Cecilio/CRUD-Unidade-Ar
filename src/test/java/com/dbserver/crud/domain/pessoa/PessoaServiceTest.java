package com.dbserver.crud.domain.pessoa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.EnderecoService;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.infra.security.TokenService;
import com.dbserver.crud.domain.autenticacao.AutenticacaoService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PessoaServiceTest {
        @InjectMocks
        private PessoaService pessoaService;

        @Mock
        private PessoaRepository pessoaRepository;

        @Mock
        private TokenService tokenService;

        @Mock
        private PasswordEncoder passwordEncoderMock;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Mock
        private AutenticacaoService autenticacaoService;

        @Mock
        private Endereco enderecoMock;
        @Mock
        private Pessoa pessoaMock;
        @Mock
        private EnderecoService enderecoService;
        @Mock
        private EnderecoRepository enderecoRepository;

        // Método Criar Pessoa
        @Test
        @DisplayName("Deve ser possível criar uma pessoa ao passar dados corretamente")
        void deveCriarPessoaPassandoTodosDados() {
                CriarEnderecoDto criarEnderecoDto = new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true);
                List<CriarEnderecoDto> enderecos = List.of(criarEnderecoDto);
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "12345678910",
                                enderecos);
                Pessoa resposta = this.pessoaService.salvarPessoa(dto);
                assertEquals(dto.nome(), resposta.getNome());
                assertEquals(dto.login(), resposta.getLogin());
                assertEquals(dto.dataNascimento().toString(), resposta.getDataNascimento().toString());
                assertEquals(dto.cpf(), resposta.getCpf());
                assertEquals(dto.enderecos().size(), resposta.getEndereco().size());
        }

        @Test
        @DisplayName("Deve ser possível criar uma pessoa com mais de um endereço")
        void deveCriarPessoaComMaisDeUmEndereco() {
                CriarEnderecoDto criarEnderecoDto = new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true);

                CriarEnderecoDto criarEnderecoDto2 = new CriarEnderecoDto("Gêmeos", "1", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", false);

                List<CriarEnderecoDto> enderecos = List.of(criarEnderecoDto, criarEnderecoDto2);
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "12345678910",
                                enderecos);
                Pessoa resposta = this.pessoaService.salvarPessoa(dto);
                assertEquals(dto.nome(), resposta.getNome());
                assertEquals(dto.login(), resposta.getLogin());
                assertEquals(dto.dataNascimento().toString(), resposta.getDataNascimento().toString());
                assertEquals(dto.cpf(), resposta.getCpf());
                assertEquals(dto.enderecos().size(), resposta.getEndereco().size());
        }

        @Test
        @DisplayName("Deve ser possível criar uma pessoa ao passar dados corretamente, sem passar endereço")
        void deveCriarPessoaSemEndereco() {
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "12345678910",
                                List.of());

                Pessoa resposta = this.pessoaService.salvarPessoa(dto);

                assertEquals(dto.nome(), resposta.getNome());
                assertEquals(dto.login(), resposta.getLogin());
                assertEquals(dto.dataNascimento().toString(), resposta.getDataNascimento().toString());
                assertEquals(dto.cpf(), resposta.getCpf());
                assertEquals(dto.enderecos().size(), resposta.getEndereco().size());
        }

        @Test
        @DisplayName("Não Deve ser possível criar uma pessoa com senha menor que 6 caracteres")
        void deveFalharAoCriarPessoaComSenhaInvalida() {
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "12345", "Pedro", LocalDate.of(2000, 12, 15),
                                "12345678910",
                                List.of());

                assertThrows(IllegalArgumentException.class, () -> this.pessoaService.salvarPessoa(dto));
        }

        @Test
        @DisplayName("Não Deve ser possível criar uma pessoa com cpf sem ter exatos 11 caracteres numéricos")
        void deveFalharAoCriarPessoaComCpfInvalido() {
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "1234567891",
                                List.of());

                assertThrows(IllegalArgumentException.class, () -> this.pessoaService.salvarPessoa(dto));
        }

        @Test
        @DisplayName("Não Deve ser possível criar uma pessoa com login nulo")
        void deveFalharAoCriarPessoaComLoginNulo() {
                CriarPessoaDto dto = new CriarPessoaDto(null, "123456", "Pedro", LocalDate.of(2000, 12, 15),
                                "12345678910",
                                List.of());

                assertThrows(IllegalArgumentException.class, () -> this.pessoaService.salvarPessoa(dto));
        }

        @Test
        @DisplayName("Não Deve ser possível criar uma pessoa com data nula")
        void deveFalharAoCriarPessoaComDataNula() {
                CriarPessoaDto dto = new CriarPessoaDto("temtr", "123456", "Pedro", null, "12345678910",
                                List.of());

                assertThrows(IllegalArgumentException.class, () -> this.pessoaService.salvarPessoa(dto));
        }

        // Método atualizar dados pessoa

        @Test
        @DisplayName("Deve ser possível atualizar uma pessoa com datos corretos")
        void deveAtualizarPessoaAoPassarDadosCorretamente() {
                List<CriarEnderecoDto> enderecos = List.of(new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true));
                CriarPessoaDto criarPessoaDto = new CriarPessoaDto("temtr", "123456", "Pedro",
                                LocalDate.of(2000, 12, 15),
                                "12345678910",
                                enderecos);
                Pessoa pessoa = new Pessoa(criarPessoaDto, this.passwordEncoderMock);
                AtualizarDadosPessoaDto novosDados = new AtualizarDadosPessoaDto("Marcos", "654321",
                                LocalDate.of(2000, 06, 15),
                                "14815587655");

                String senhaCodificada = this.passwordEncoder.encode("654321");
                when(this.passwordEncoderMock.encode(any(CharSequence.class))).thenReturn(senhaCodificada);
                when(this.passwordEncoderMock.matches("654321", senhaCodificada)).thenReturn(true);

                this.pessoaService.atualizarDadosPessoa(novosDados, pessoa);
                assertEquals("Marcos", pessoa.getNome());
                assertTrue(pessoa.compararSenha("654321", this.passwordEncoderMock));
                assertEquals(LocalDate.of(2000, 06, 15).toString(), pessoa.getDataNascimento().toString());
                assertEquals("14815587655", pessoa.getCpf());

        }

        @Test
        @DisplayName("Deve ser possível atualizar somente um campo")
        void deveAtualizarPessoaAoPassarSomenteUmCampo() {
                List<CriarEnderecoDto> enderecos = List.of(new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true));
                CriarPessoaDto criarPessoaDto = new CriarPessoaDto("temtr", "123456", "Pedro",
                                LocalDate.of(2000, 12, 15),
                                "12345678910",
                                enderecos);
                Pessoa pessoa = new Pessoa(criarPessoaDto, this.passwordEncoder);
                AtualizarDadosPessoaDto novosDados = new AtualizarDadosPessoaDto(null, null, null,
                                "14815587655");

                String senhaCodificada = this.passwordEncoder.encode("123456");
                when(this.passwordEncoderMock.encode(any(CharSequence.class))).thenReturn(senhaCodificada);
                when(this.passwordEncoderMock.matches("123456", senhaCodificada)).thenReturn(true);

                this.pessoaService.atualizarDadosPessoa(novosDados, pessoa);
                assertEquals("Pedro", pessoa.getNome());
                assertTrue(pessoa.compararSenha("123456", this.passwordEncoderMock));
                assertEquals(LocalDate.of(2000, 12, 15).toString(), pessoa.getDataNascimento().toString());
                assertEquals("14815587655", pessoa.getCpf());

        }

        @ParameterizedTest
        @DisplayName("Deve falhar ao tentar atualizar pessoa com dados inválidos: senha = {0}, cpf = {1}, nome = {2}")
        @CsvSource({
                        // nome, senha, cpf
                        " , 1234, ",
                        " ,  , 1234567891a",
                        "eu,  ,  "
        })
        void deveFalharAoAtualizarPessoaComDadosInvalidos(String nome, String senha, String cpf) {
                List<CriarEnderecoDto> enderecos = List.of(new CriarEnderecoDto("Gêmeos", "3", "Ribeiro de Abreu",
                                "Belo Horizonte", "Ribeiro de Abreu", "31872140", true));
                CriarPessoaDto criarPessoaDto = new CriarPessoaDto("temtr", "123456", "Pedro",
                                LocalDate.of(2000, 12, 15),
                                "12345678910",
                                enderecos);
                Pessoa pessoa = new Pessoa(criarPessoaDto, this.passwordEncoder);
                AtualizarDadosPessoaDto novosDados = new AtualizarDadosPessoaDto(nome, senha, null, cpf);

                assertThrows(IllegalArgumentException.class,
                                () -> this.pessoaService.atualizarDadosPessoa(novosDados, pessoa));
        }

}
