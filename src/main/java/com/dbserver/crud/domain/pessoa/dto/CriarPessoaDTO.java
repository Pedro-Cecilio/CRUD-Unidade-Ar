package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarPessoaDTO(
        @NotBlank
        String nome,

        @NotNull
        LocalDate dataNascimento,

        @NotBlank
        @Size(min = 11, max = 11, message = "O cpf deve conter 11 caracteres")
        String cpf
        
) {

}
