package com.dbserver.crud.domain.pessoa;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.EnderecoService;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;

@Service
public class PessoaService {

    private PessoaRepository pessoaRepository;
    private PasswordEncoder passwordEnconder;
    private EnderecoRepository enderecoRepository;
    private EnderecoService enderecoService;

    public PessoaService(PessoaRepository pessoaRepository, PasswordEncoder passwordEnconder,
            EnderecoRepository enderecoRepository, EnderecoService enderecoService) {
        this.pessoaRepository = pessoaRepository;
        this.passwordEnconder = passwordEnconder;
        this.enderecoRepository = enderecoRepository;
        this.enderecoService = enderecoService;
    }

    public Pessoa salvarPessoa(CriarPessoaDto criarPessoaDTO) {
        Pessoa pessoa = new Pessoa(criarPessoaDTO, this.passwordEnconder);
        List<CriarEnderecoDto> enderecos = criarPessoaDTO.enderecos();
        this.pessoaRepository.save(pessoa);
        if (enderecos != null && !enderecos.isEmpty()) {
            enderecos.forEach(endereco -> {
                Endereco novoEndereco = new Endereco(endereco);
                novoEndereco.setPessoa(pessoa);
                if (endereco.principal().booleanValue()) {
                    this.enderecoService.definirComoEnderecoPrincipal(pessoa.getId(), novoEndereco);
                } else {
                    this.enderecoRepository.save(novoEndereco);
                }
                pessoa.setEndereco(novoEndereco);
            });
        }
        this.pessoaRepository.save(pessoa);
        return pessoa;

    }

    public Pessoa atualizarDadosPessoa(AtualizarDadosPessoaDto atualizarDadosPessoaDto, Pessoa pessoa) {
        pessoa.atualizarDados(atualizarDadosPessoaDto, this.passwordEnconder);
        return this.pessoaRepository.save(pessoa);
    }

    public List<PessoaRespostaDto> pegarTodasPessoas(Pageable pageable) {
        Page<Pessoa> pessoas = this.pessoaRepository.findAll(pageable);
        return pessoas.stream().map(PessoaRespostaDto::new).toList();
    }

    public void deletarPessoa(Long pessoaid) {
        Optional<Pessoa> pessoaEncontrada = this.pessoaRepository.findById(pessoaid);
        if(pessoaEncontrada.isEmpty()){
            throw new NoSuchElementException("Pessoa não encontrada.");
        }
        this.pessoaRepository.delete(pessoaEncontrada.get());
    }

    public Pessoa pegarPessoaLogada() {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(this.pegarIdDaPessoaLogada());
        if (pessoa.isPresent()) {
            return pessoa.get();
        }
        throw new NoSuchElementException("Usuário não encontrado");
    }

    public Long pegarIdDaPessoaLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pessoa pessoaLogada = (Pessoa) authentication.getPrincipal();
        return pessoaLogada.getId();
    }
}
