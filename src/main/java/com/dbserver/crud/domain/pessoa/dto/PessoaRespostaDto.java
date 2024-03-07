package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.pessoa.Pessoa;

public record PessoaRespostaDto(
        Long id,

        String login,

        String nome,

        LocalDate dataNascimento,

        String cpf,

        List<Endereco> enderecos
        ) {
    public PessoaRespostaDto(Pessoa pessoa) {
        this(pessoa.getId(), pessoa.getLogin(), pessoa.getNome(), pessoa.getDataNascimento(),
                pessoa.getCpf(), pessoa.getEndereco());
    }
}
