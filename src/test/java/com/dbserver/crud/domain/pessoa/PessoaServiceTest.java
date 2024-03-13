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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.EnderecoService;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
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
        private Pageable pageable;
        @Mock
        private Endereco enderecoMock;
        @Mock
        private Pessoa pessoaMock;
        @Mock
        private Pessoa pessoaMock2;

        @Mock
        private EnderecoService enderecoService;
        @Mock
        private EnderecoRepository enderecoRepository;

        @Autowired
        private PasswordEncoder passwordEnconder;

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

        @Test
        @DisplayName("Deve buscar todas pessoas")
        void deveBuscarTodasPessoas() {
                this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15),
                                "12345678910",
                                this.passwordEnconder);
                this.pessoaMock2 = new Pessoa(2L, "user456", "senha456", "Maria", LocalDate.of(1988, 9, 20),
                                "98765432109", this.passwordEncoder);

                List<Pessoa> pessoas = List.of(pessoaMock, pessoaMock2);
                Page<Pessoa> pagePessoas = new PageImpl<>(pessoas);

                when(this.pessoaRepository.findAll(pageable)).thenReturn(pagePessoas);

                List<PessoaRespostaDto> resposta = this.pessoaService.pegarTodasPessoas(pageable);

                // Assertivas para o primeiro objeto Pessoa
                assertEquals(pessoaMock.getId(), resposta.get(0).id());
                assertEquals(pessoaMock.getLogin(), resposta.get(0).login());
                assertEquals(pessoaMock.getNome(), resposta.get(0).nome());
                assertEquals(pessoaMock.getDataNascimento(), resposta.get(0).dataNascimento());
                assertEquals(pessoaMock.getCpf(), resposta.get(0).cpf());

                // Assertivas para o segundo objeto Pessoa, se necessário
                assertEquals(pessoaMock2.getId(), resposta.get(1).id());
                assertEquals(pessoaMock2.getLogin(), resposta.get(1).login());
                assertEquals(pessoaMock2.getNome(), resposta.get(1).nome());
                assertEquals(pessoaMock2.getDataNascimento(), resposta.get(1).dataNascimento());
                assertEquals(pessoaMock2.getCpf(), resposta.get(1).cpf());

                assertEquals(pessoas.size(), resposta.size());

        }

        @Test
        @DisplayName("Deve retornar lista vazia ao buscar todos endereços quando não houver endereço cadastrado")
        void deveRetornarListaVaziaAoBuscarTodosEnderecos() {

                List<Pessoa> pessoas = List.of(pessoaMock, pessoaMock2);
                Page<Pessoa> pagePessoas = new PageImpl<>(pessoas);

                when(this.pessoaRepository.findAll(pageable)).thenReturn(pagePessoas);

                List<PessoaRespostaDto> resposta = this.pessoaService.pegarTodasPessoas(pageable);

                assertEquals(pessoas.size(), resposta.size());

        }

}
