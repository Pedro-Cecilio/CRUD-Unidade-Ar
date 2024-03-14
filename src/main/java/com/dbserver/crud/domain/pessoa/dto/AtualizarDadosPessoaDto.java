package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;

import com.dbserver.crud.utils.Utils;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarDadosPessoaDto(
        @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
        String nome,

        String senha,

        LocalDate dataNascimento,

        @Pattern(regexp = Utils.REGEX_CPF, message = "Cpf deve conter 11 caracteres numéricos" )
        String cpf

        ) {

}
