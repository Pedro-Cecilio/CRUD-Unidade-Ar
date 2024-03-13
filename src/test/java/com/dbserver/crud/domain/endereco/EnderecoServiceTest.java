package com.dbserver.crud.domain.endereco;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoRespostaDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class EnderecoServiceTest {

        @InjectMocks
        private EnderecoService enderecoService;

        @Mock
        private PessoaService pessoaService;

        @Mock
        private EnderecoRepository enderecoRepository;

        @Mock
        private Endereco enderecoMockPrincipalFalse;

        @Mock
        private Endereco enderecoMockPrincipalTrue;

        @Mock
        private Pessoa pessoaMock;

        @Mock
        private CriarEnderecoDto criarEnderecoDtoMock;

        @Mock
        private AtualizarEnderecoDto atualizarEnderecoDtoMock;

        @Autowired
        private PasswordEncoder passwordEnconder;

        @Mock
        private Pageable pageable;

        @BeforeEach
        void setUp() {
                this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15),
                                "12345678910",
                                this.passwordEnconder);

                this.enderecoMockPrincipalFalse = new Endereco(1L, "Rua das Rosas", "123", "Centro", "São Paulo", "SP",
                                "12345678",
                                false, this.pessoaMock);
                this.enderecoMockPrincipalTrue = new Endereco(2L, "Rua das Rosas", "123", "Centro", "São Paulo", "SP",
                                "12345678",
                                true, this.pessoaMock);
                this.atualizarEnderecoDtoMock = new AtualizarEnderecoDto("Nova Rua", "456", "Bairro Novo",
                                "Rio de Janeiro", "RJ", "98765432", true);

                this.criarEnderecoDtoMock = new CriarEnderecoDto("Rua das Flores", "123", "Centro", "São Paulo", "SP",
                                "12345678", false);
        }

        @Test
        @DisplayName("Deve ser possível definir um endereço como principal quando for o primeiro endereço principal de pessoa")
        void deveSerPossivelDefinirUmEnderecoComoPrincipalQuandoForOPrimeiroPrincipal() {

                when(this.enderecoRepository.findByPessoaIdAndPrincipal(anyLong(), eq(true)))
                                .thenReturn(Optional.empty());
                Endereco endereco = this.enderecoService.definirComoEnderecoPrincipal(1L,
                                this.enderecoMockPrincipalFalse);
                verify(this.enderecoRepository).save(endereco);
                assertEquals(true, endereco.getPrincipal());
        }

        @Test
        @DisplayName("Deve ser possível definir um endereço como principal quando não for o primeiro endereço principal de pessoa")
        void deveSerPossivelDefinirUmEnderecoComoPrincipalQuandoNaoForOPrimeiroPrincipal() {

                when(this.enderecoRepository.findByPessoaIdAndPrincipal(anyLong(), eq(true)))
                                .thenReturn(Optional.of(enderecoMockPrincipalTrue));
                Endereco endereco = this.enderecoService.definirComoEnderecoPrincipal(2L, enderecoMockPrincipalFalse);
                verify(this.enderecoRepository).save(endereco);
                verify(this.enderecoRepository, times(2)).save(any(Endereco.class));
                assertEquals(true, endereco.getPrincipal());
        }

        @Test
        @DisplayName("Deve ser possível criar um novo endereço corretamente")
        void deveSerPossivelCriarUmNovoEndereco() {

                EnderecoRespostaDto resposta = this.enderecoService.criarNovoEndereco(criarEnderecoDtoMock, pessoaMock);

                verify(this.enderecoRepository).save(any(Endereco.class));
                assertEquals(criarEnderecoDtoMock.rua(), resposta.rua());
                assertEquals(criarEnderecoDtoMock.numero(), resposta.numero());
                assertEquals(criarEnderecoDtoMock.bairro(), resposta.bairro());
                assertEquals(criarEnderecoDtoMock.cidade(), resposta.cidade());
                assertEquals(criarEnderecoDtoMock.estado(), resposta.estado());
                assertEquals(criarEnderecoDtoMock.cep(), resposta.cep());
                assertEquals(criarEnderecoDtoMock.principal(), resposta.principal());
        }

        private static Stream<Arguments> argumentosDadosInvalidosParaCriarEndereco() {
                return Stream.of(
                                Arguments.of(" ", "123", "Bairro Central", "Belo Horizonte", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "abc", "Bairro Central", "Belo Horizonte", "MG",
                                                "12345678"),
                                Arguments.of("Rua das Flores", "123", " ", "Belo Horizonte", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", " ", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", " ",
                                                "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", "MG",
                                                "31872"));
        }

        @ParameterizedTest
        @MethodSource("argumentosDadosInvalidosParaCriarEndereco")
        @DisplayName("Deve falhar ao tentar criar com dados inválidos: rua = {1}, numero = {2}, bairro = {3}, cidade = {4}, estado = {5}, cep = {6}")
        void deveFalharAoTentarEnderecoCriarComDadosInvalidos(String rua, String numero, String bairro, String cidade,
                        String estado, String cep) {

                this.criarEnderecoDtoMock = new CriarEnderecoDto(rua, numero, bairro, cidade, estado, cep, false);
                assertThrows(IllegalArgumentException.class, () -> enderecoService
                                .criarNovoEndereco(this.criarEnderecoDtoMock, this.pessoaMock));
        }

        @Test
        @DisplayName("Deve ser possível atualizar um endereço corretamente")
        void deveSerPossivelAtualizarUmNovoEndereco() {

                when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong()))
                                .thenReturn(Optional.of(this.enderecoMockPrincipalFalse));

                EnderecoRespostaDto resposta = this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L,
                                this.pessoaMock);

                verify(this.enderecoRepository).save(any(Endereco.class));
                assertEquals(atualizarEnderecoDtoMock.rua(), resposta.rua());
                assertEquals(atualizarEnderecoDtoMock.numero(), resposta.numero());
                assertEquals(atualizarEnderecoDtoMock.bairro(), resposta.bairro());
                assertEquals(atualizarEnderecoDtoMock.cidade(), resposta.cidade());
                assertEquals(atualizarEnderecoDtoMock.estado(), resposta.estado());
                assertEquals(atualizarEnderecoDtoMock.cep(), resposta.cep());
                assertEquals(atualizarEnderecoDtoMock.principal(), resposta.principal());
        }

        @Test
        @DisplayName("Deve lançar um NoSuchElementException ao não encontrar o endereço a ser atualizado")
        void deveDevolverNoSuchElementAoNaoEncontrarElemento() {

                when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong())).thenReturn(Optional.empty());

                assertThrows(NoSuchElementException.class,
                                () -> this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock));
        }

        private static Stream<Arguments> argumentosDadosInvalidosParaAtualizarEndereco() {
                return Stream.of(
                                Arguments.of(" ", "123", "Bairro Central", "Belo Horizonte", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "abc", "Bairro Central", "Belo Horizonte", "MG",
                                                "12345678"),
                                Arguments.of("Rua das Flores", "123", " ", "Belo Horizonte", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", " ", "MG", "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", " ",
                                                "12345678"),
                                Arguments.of("Rua das Flores", "123", "Bairro Central", "Belo Horizonte", "MG",
                                                "31872"));
        }

        @ParameterizedTest
        @DisplayName("Deve falhar ao tentar atualizar com dados inválidos: rua = {1}, numero = {2}, bairro = {3}, cidade = {4}, estado = {5}, cep = {6}")
        @MethodSource("argumentosDadosInvalidosParaAtualizarEndereco")
        void deveFalharAoAtualizarComDadosInvalidos(String rua, String numero, String bairro, String cidade,
                        String estado, String cep) {

                this.atualizarEnderecoDtoMock = new AtualizarEnderecoDto(rua, numero, bairro, cidade, estado, cep,
                                true);

                when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong()))
                                .thenReturn(Optional.of(this.enderecoMockPrincipalFalse));

                assertThrows(IllegalArgumentException.class,
                                () -> this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock));
        }

        @Test
        @DisplayName("Deve buscar todos endereços")
        void deveBuscarTodosEnderecos() {
                List<Endereco> enderecos = List.of(enderecoMockPrincipalFalse, enderecoMockPrincipalTrue);
                Page<Endereco> pageEnderecos = new PageImpl<>(enderecos);

                when(this.enderecoRepository.findAllByPessoaId(pageable, 1L)).thenReturn(pageEnderecos);

                List<EnderecoRespostaDto> resposta = this.enderecoService.pegarTodosEnderecos(pageable, 1L);

                assertEquals(enderecoMockPrincipalFalse.getId(), resposta.get(0).id());
                assertEquals(enderecoMockPrincipalFalse.getRua(), resposta.get(0).rua());
                assertEquals(enderecoMockPrincipalFalse.getNumero(), resposta.get(0).numero());
                assertEquals(enderecoMockPrincipalFalse.getBairro(), resposta.get(0).bairro());
                assertEquals(enderecoMockPrincipalFalse.getCidade(), resposta.get(0).cidade());
                assertEquals(enderecoMockPrincipalFalse.getEstado(), resposta.get(0).estado());
                assertEquals(enderecoMockPrincipalFalse.getCep(), resposta.get(0).cep());
                assertEquals(enderecoMockPrincipalFalse.getPrincipal(), resposta.get(0).principal());

                assertEquals(enderecoMockPrincipalTrue.getId(), resposta.get(1).id());
                assertEquals(enderecoMockPrincipalTrue.getRua(), resposta.get(1).rua());
                assertEquals(enderecoMockPrincipalTrue.getNumero(), resposta.get(1).numero());
                assertEquals(enderecoMockPrincipalTrue.getBairro(), resposta.get(1).bairro());
                assertEquals(enderecoMockPrincipalTrue.getCidade(), resposta.get(1).cidade());
                assertEquals(enderecoMockPrincipalTrue.getEstado(), resposta.get(1).estado());
                assertEquals(enderecoMockPrincipalTrue.getCep(), resposta.get(1).cep());
                assertEquals(enderecoMockPrincipalTrue.getPrincipal(), resposta.get(1).principal());

                assertEquals(enderecos.size(), resposta.size());

        }

        @Test
        @DisplayName("Deve retornar lista vazia ao buscar todos endereços quando não houver endereço cadastrado")
        void deveRetornarListaVaziaAoBuscarTodosEnderecos() {
                List<Endereco> enderecos = List.of();
                Page<Endereco> pageEnderecos = new PageImpl<>(enderecos);

                when(this.enderecoRepository.findAllByPessoaId(pageable, 1L)).thenReturn(pageEnderecos);

                List<EnderecoRespostaDto> resposta = this.enderecoService.pegarTodosEnderecos(pageable, 1L);

                assertEquals(enderecos.size(), resposta.size());

        }

        @Test
        @DisplayName("Deve ser possivel deletar um endereço existente, pertencente à pessoa logada na aplicação")
        void deveDeletarUmEnderecoExistente() {
                when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong()))
                                .thenReturn(Optional.of(this.enderecoMockPrincipalFalse));
                assertDoesNotThrow(() -> this.enderecoService.deletarEndereco(anyLong(), anyLong()));
                verify(this.enderecoRepository).delete(this.enderecoMockPrincipalFalse);
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao não encontrar endereço a ser deletado")
        void deveFalharAoDeletarUmEnderecoQuandoNãoForEncontrado() {
                when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong())).thenReturn(Optional.empty());
                Executable deletarEndereco = () -> this.enderecoService.deletarEndereco(anyLong(), anyLong());

                assertThrows(NoSuchElementException.class, deletarEndereco);
        }

        @Test
        @DisplayName("Deve ser possivel buscar o endereco principal pertencente à pessoa logada na aplicação")
        void devePegarEnderecoPrincipalExistente() {
                when(this.enderecoRepository.findFirstByPessoaIdAndPrincipalTrue(anyLong()))
                                .thenReturn(Optional.of(this.enderecoMockPrincipalTrue));
                EnderecoRespostaDto resposta = this.enderecoService.pegarEnderecoPrincipal(anyLong());
                assertEquals(this.enderecoMockPrincipalTrue.getPrincipal(), resposta.principal());
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar o endereco principal pessoa que não o possui")
        void devefalharAoPegarEnderecoPrincipalInexistente() {
                when(this.enderecoRepository.findFirstByPessoaIdAndPrincipalTrue(anyLong()))
                                .thenReturn(Optional.empty());
                Executable pegarEnderecoPrincipal = () -> this.enderecoService.pegarEnderecoPrincipal(anyLong());
                assertThrows(NoSuchElementException.class, pegarEnderecoPrincipal );
        }
}
