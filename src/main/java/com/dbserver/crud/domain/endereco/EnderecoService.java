package com.dbserver.crud.domain.endereco;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoRespostaDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EnderecoService {
    private EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco definirComoEnderecoPrincipal(Long pessoaId, Endereco endereco) {
        Optional<Endereco> enderecoPrincipalTrue = this.enderecoRepository.findByPessoaIdAndPrincipal(pessoaId, true);
        if (enderecoPrincipalTrue.isPresent() && !enderecoPrincipalTrue.get().getId().equals(endereco.getId())) {
            enderecoPrincipalTrue.get().setPrincipal(false);
            this.enderecoRepository.save(enderecoPrincipalTrue.get());
        }
        endereco.setPrincipal(true);
        this.enderecoRepository.save(endereco);
        return endereco;
    }

    public EnderecoRespostaDto criarNovoEndereco(CriarEnderecoDto criarEnderecoDto, Pessoa pessoa) {
        Endereco novoEndereco = new Endereco(criarEnderecoDto);
        novoEndereco.setPessoa(pessoa);
        if (novoEndereco.getPrincipal().equals(true)) {
            this.definirComoEnderecoPrincipal(pessoa.getId(), novoEndereco);
        } else {
            this.enderecoRepository.save(novoEndereco);
        }
        return new EnderecoRespostaDto(novoEndereco);
    }

    public EnderecoRespostaDto atualizarEndereco(AtualizarEnderecoDto novosDados, Long enderecoId, Pessoa pessoa) {
        Optional<Endereco> endereco = this.enderecoRepository.findByIdAndPessoaId(enderecoId, pessoa.getId());
        if (endereco.isEmpty()) {
            throw new NoSuchElementException("Endereço não encontrado.");
        }
        Endereco enderecoValido = endereco.get();
        enderecoValido.atualizarDados(novosDados);
        if (novosDados.principal().booleanValue() && !novosDados.principal().equals(enderecoValido.getPrincipal())) {
            this.definirComoEnderecoPrincipal(pessoa.getId(), enderecoValido);
            return new EnderecoRespostaDto(enderecoValido);
        }
        if (!novosDados.principal().booleanValue() && !novosDados.principal().equals(enderecoValido.getPrincipal())) {
            enderecoValido.setPrincipal(false);
        }
        this.enderecoRepository.save(enderecoValido);
        return new EnderecoRespostaDto(enderecoValido);
    }

    public List<EnderecoRespostaDto> pegarTodosEnderecos(Pageable pageable) {
        Page<Endereco> enderecos = this.enderecoRepository.findAll(pageable);
        return enderecos.stream().map(EnderecoRespostaDto::new).toList();
    }
}
