package com.dbserver.crud.domain.endereco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoRespostaDto;
import com.dbserver.crud.domain.pessoa.Pessoa;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class EnderecoServiceTest {

    @InjectMocks
    private EnderecoService enderecoService;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private Endereco enderecoMock;
    @Mock
    private Endereco enderecoMock2;

    @Mock
    private Pessoa pessoaMock;

    @Mock
    private CriarEnderecoDto criarEnderecoDtoMock;
    @Mock
    private AtualizarEnderecoDto atualizarEnderecoDtoMock;

    @Autowired
    private PasswordEncoder passwordEnconder;

    @Test
    @DisplayName("Deve ser possível definir um endereço como principal quando for o primeiro endereço principal de pessoa")
    void deveSerPossivelDefinirUmEnderecoComoPrincipalQuandoForOPrimeiroPrincipal() {
        
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);
        this.enderecoMock = new Endereco(1L, "Rua das Flores", "123", "Centro", "São Paulo", "SP", "12345678", false, this.pessoaMock);

        when(this.enderecoRepository.findByPessoaIdAndPrincipal(anyLong(), eq(true))).thenReturn(Optional.empty());
        Endereco endereco = this.enderecoService.definirComoEnderecoPrincipal(1L, enderecoMock);
        verify(this.enderecoRepository).save(endereco);
        assertEquals(true, endereco.getPrincipal());
    }
    @Test
    @DisplayName("Deve ser possível definir um endereço como principal quando não for o primeiro endereço principal de pessoa")
    void deveSerPossivelDefinirUmEnderecoComoPrincipalQuandoNaoForOPrimeiroPrincipal() {
        
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);
        this.enderecoMock = new Endereco(2L, "Rua das Flores", "123", "Centro", "São Paulo", "SP", "12345678", false, this.pessoaMock);
        this.enderecoMock2 = new Endereco(1L, "Rua das Rosas", "123", "Centro", "São Paulo", "SP", "12345678", true, this.pessoaMock);

        when(this.enderecoRepository.findByPessoaIdAndPrincipal(anyLong(), eq(true))).thenReturn(Optional.of(enderecoMock2));
        Endereco endereco = this.enderecoService.definirComoEnderecoPrincipal(2L, enderecoMock);
        verify(this.enderecoRepository).save(endereco);
        verify(this.enderecoRepository, times(2)).save(any(Endereco.class));
        assertEquals(true, endereco.getPrincipal());
    }

    @Test
    @DisplayName("Deve ser possível criar um novo endereço corretamente")
    void deveSerPossivelCriarUmNovoEndereco() {
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);

        this.criarEnderecoDtoMock = new CriarEnderecoDto("Rua das Flores", "123", "Centro", "São Paulo", "SP",
                "12345678", false);

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

    @ParameterizedTest
    @DisplayName("Deve falhar ao tentar criar com dados inválidos: rua = {1}, numero = {2}, bairro = {3}, cidade = {4}, estado = {5}, cep = {6}")
    @CsvSource({
            // rua, numero, bairro, cidade, estado, cep
            " , 123, Bairro Central, Belo Horizonte, MG, 12345678",
            "Rua das Flores, abc, Bairro Central, Belo Horizonte, MG, 12345678",
            "Rua das Flores, 123,  , Belo Horizonte, MG, 12345678",
            "Rua das Flores, 123, Bairro Central,  , MG, 12345678",
            "Rua das Flores, 123, Bairro Central, Belo Horizonte,  , 12345678",
            "Rua das Flores, 123, Bairro Central, Belo Horizonte, MG, 31872"
    })
    void deveFalharAoTentarEnderecoCriarComDadosInvalidos(String rua, String numero, String bairro, String cidade,
            String estado, String cep) {
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);

        this.criarEnderecoDtoMock = new CriarEnderecoDto(rua, numero, bairro, cidade, estado, cep, false);
        assertThrows(IllegalArgumentException.class, () -> enderecoService
                .criarNovoEndereco(this.criarEnderecoDtoMock, this.pessoaMock));
    }

    @Test
    @DisplayName("Deve ser possível atualizar um endereço corretamente")
    void deveSerPossivelAtualizarUmNovoEndereco() {
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);
        this.enderecoMock = new Endereco(1L, "Rua das Flores", "123", "Centro", "São Paulo", "SP", "12345678", true, this.pessoaMock);
        this.atualizarEnderecoDtoMock = new AtualizarEnderecoDto("Nova Rua", "456", "Bairro Novo", "Rio de Janeiro", "RJ", "98765432", true);

        when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong())).thenReturn(Optional.of(this.enderecoMock));
        
        EnderecoRespostaDto resposta = this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock);

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
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);
        this.atualizarEnderecoDtoMock = new AtualizarEnderecoDto("Nova Rua", "456", "Bairro Novo", "Rio de Janeiro", "RJ", "98765432", true);

        when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong())).thenReturn(Optional.empty());
        
        // EnderecoRespostaDto resposta = this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock);

        assertThrows(NoSuchElementException.class, ()-> this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock));
    }

    @ParameterizedTest
    @DisplayName("Deve falhar ao tentar atualizar com dados inválidos: rua = {1}, numero = {2}, bairro = {3}, cidade = {4}, estado = {5}, cep = {6}")
    @CsvSource({
            // rua, numero, bairro, cidade, estado, cep
            " , 123, Bairro Central, Belo Horizonte, MG, 12345678",
            "Rua das Flores, abc, Bairro Central, Belo Horizonte, MG, 12345678",
            "Rua das Flores, 123,  , Belo Horizonte, MG, 12345678",
            "Rua das Flores, 123, Bairro Central,  , MG, 12345678",
            "Rua das Flores, 123, Bairro Central, Belo Horizonte,  , 12345678",
            "Rua das Flores, 123, Bairro Central, Belo Horizonte, MG, 31872"
    })
    void deveFalharAoAtualizarComDadosInvalidos(String rua, String numero, String bairro, String cidade,
    String estado, String cep) {
        this.pessoaMock = new Pessoa(1L, "usuario123", "senha123", "João", LocalDate.of(1990, 5, 15), "12345678910",
                this.passwordEnconder);
        this.enderecoMock = new Endereco(1L, "Rua das Flores", "123", "Centro", "São Paulo", "SP", "12345678", true, this.pessoaMock);

        this.atualizarEnderecoDtoMock = new AtualizarEnderecoDto(rua, numero, bairro, cidade, estado, cep, true);

        when(this.enderecoRepository.findByIdAndPessoaId(anyLong(), anyLong())).thenReturn(Optional.of(this.enderecoMock));
        
        assertThrows(IllegalArgumentException.class, ()-> this.enderecoService.atualizarEndereco(atualizarEnderecoDtoMock, 1L, pessoaMock));
    }


}
