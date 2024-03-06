package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarPessoaDto(
        @NotBlank
        String nome,

        @NotNull
        LocalDate dataNascimento,

        @NotBlank
        @Size(min = 11, max = 11, message = "O cpf deve conter 11 caracteres")
        String cpf,

        List<CriarEnderecoDto> endereco
) {

}
