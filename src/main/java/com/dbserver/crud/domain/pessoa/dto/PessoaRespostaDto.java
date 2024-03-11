package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.dto.EnderecoRespostaDto;
import com.dbserver.crud.domain.pessoa.Pessoa;

public record PessoaRespostaDto(
        Long id,

        String login,

        String nome,

        LocalDate dataNascimento,

        String cpf,

        List<EnderecoRespostaDto> enderecos
        ) {
    public PessoaRespostaDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getLogin(), pessoa.getNome(), pessoa.getDataNascimento(),
                pessoa.getCpf(), gerarEnderecoRespostaDtos(pessoa.getEndereco()));
    }
    private static List<EnderecoRespostaDto> gerarEnderecoRespostaDtos(List<Endereco> enderecos){
        List<EnderecoRespostaDto> enderecosResposta = new ArrayList<>();
        enderecos.forEach(endereco->{
            EnderecoRespostaDto enderecoResposta = new EnderecoRespostaDto(endereco);
            enderecosResposta.add(enderecoResposta);
        });
        return enderecosResposta;

    }
}
