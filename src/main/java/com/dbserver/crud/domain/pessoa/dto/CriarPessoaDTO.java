package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.infra.jsonDeserializer.LocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarPessoaDto(
        @NotBlank
        @Size(min = 5, message = "Deve ter no mínimo 5 caracteres")
        String login,

        @NotBlank
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String senha,

        @NotBlank
        String nome,

        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate dataNascimento,

        @NotBlank
        @Size(min = 11, max = 11, message = "O cpf deve conter 11 caracteres")
        String cpf,

        List<CriarEnderecoDto> enderecos
) {

}
