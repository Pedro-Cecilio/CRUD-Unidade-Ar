package com.dbserver.crud.domain.pessoa;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoService;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import com.dbserver.crud.infra.security.TokenService;

@Service
public class PessoaService {

    private PessoaRepository pessoaRepository;
    private PasswordEncoder passwordEnconder;
    private TokenService tokenService;
    private EnderecoRepository enderecoRepository;

    public PessoaService(PessoaRepository pessoaRepository, PasswordEncoder passwordEnconder,
            TokenService tokenService, EnderecoRepository enderecoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.passwordEnconder = passwordEnconder;
        this.tokenService = tokenService;
        this.enderecoRepository = enderecoRepository;
    }

    public Pessoa salvarPessoa(CriarPessoaDto criarPessoaDTO) {
        Pessoa pessoa = new Pessoa(criarPessoaDTO, this.passwordEnconder);
        List<CriarEnderecoDto> enderecos = criarPessoaDTO.enderecos();
        if(!enderecos.isEmpty()) {
            enderecos.forEach(endereco ->{
                Endereco novoEndereco = new Endereco(endereco);
                novoEndereco.setPessoa(pessoa);
                this.enderecoRepository.save(novoEndereco);
                pessoa.setEndereco(novoEndereco);
            });
        }
        this.pessoaRepository.save(pessoa);
        return pessoa;

    }

    public PessoaRespostaDto criarPessoaRespostaDto(Pessoa pessoa) {
        return new PessoaRespostaDto(pessoa);
    }

    public Pessoa atualizarDadosPessoa(AtualizarDadosPessoaDto atualizarDadosPessoaDto, Pessoa pessoa) {
        pessoa.atualizarDados(atualizarDadosPessoaDto, this.passwordEnconder);
        return this.pessoaRepository.save(pessoa);
    }

    public Pessoa pegarPessoaLogada() {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(this.tokenService.pegarIdDaPessoaLogada());
        if (pessoa.isPresent()) {
            return pessoa.get();
        }
        throw new NoSuchElementException("Usuário não encontrado");
    }
}
