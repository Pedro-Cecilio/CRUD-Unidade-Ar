package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.pessoa.Pessoa;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record AtualizarDadosPessoaDto(
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String nome,

        String senha,

        LocalDate dataNascimento,

        @Size(min = 11, max = 11, message = "O cpf deve conter 11 caracteres")
        String cpf

        ) {

}
