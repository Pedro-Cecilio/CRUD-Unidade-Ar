package com.dbserver.crud.domain.endereco.dto;


import java.util.Optional;
import org.springframework.stereotype.Service;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.pessoa.Pessoa;
import java.util.NoSuchElementException;

@Service
public class EnderecoService {
    private EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco definirComoEnderecoPrincipal(Long pessoaId, Endereco endereco) {
        Optional<Endereco> enderecoPrincipal = this.enderecoRepository.findByPessoaIdAndPrincipal(pessoaId, true);
        if(enderecoPrincipal.isPresent() && !enderecoPrincipal.get().getId().equals(endereco.getId()) ){
            enderecoPrincipal.get().setPrincipal(false);
            this.enderecoRepository.save(enderecoPrincipal.get());
            endereco.setPrincipal(true);
            this.enderecoRepository.save(endereco);
            return endereco;
        }
        endereco.setPrincipal(true);
        this.enderecoRepository.save(endereco);
        return endereco;
    }

    public EnderecoRespostaDto criarNovoEndereco(CriarEnderecoDto criarEnderecoDto, Pessoa pessoa){
        Endereco novoEndereco = new Endereco(criarEnderecoDto);
        novoEndereco.setPessoa(pessoa);
        if(novoEndereco.getPrincipal().equals(true)){
            this.definirComoEnderecoPrincipal(pessoa.getId(), novoEndereco);
        }else{
            this.enderecoRepository.save(novoEndereco);
        }      
        return new EnderecoRespostaDto(novoEndereco);
    }
    
    public EnderecoRespostaDto atualizarEndereco(AtualizarEnderecoDto novosDados, Long enderecoId, Pessoa pessoa){
        Optional<Endereco> endereco = this.enderecoRepository.findByIdAndPessoaId(enderecoId, pessoa.getId());
        if(endereco.isEmpty()){
            throw new NoSuchElementException("Endereço não encontrado.");
        }
        Endereco enderecoValido = endereco.get();
        enderecoValido.atualizarDados(novosDados);
        if(novosDados.principal().equals(true) && !novosDados.principal().equals(enderecoValido.getPrincipal())) {
            this.definirComoEnderecoPrincipal(pessoa.getId(), enderecoValido);
        }
        if(novosDados.principal().equals(false) && !novosDados.principal().equals(enderecoValido.getPrincipal())) {
            enderecoValido.setPrincipal(false);
        }
        return new EnderecoRespostaDto(enderecoValido);
    }
}
