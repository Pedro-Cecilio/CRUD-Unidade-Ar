package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarDadosPessoaDto(
        @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
        String nome,

        String senha,

        LocalDate dataNascimento,

        @Pattern(regexp = "\\b\\d{11}\\b", message = "O cpf deve conter 11 caracteres numéricos" )
        String cpf

        ) {

}
