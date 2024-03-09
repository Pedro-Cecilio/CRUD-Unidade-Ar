package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.infra.jsonDeserializer.LocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
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
        @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
        String nome,

        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate dataNascimento,

        @NotBlank
        @Pattern(regexp = "\\b\\d{11}\\b", message = "O cpf deve conter 11 caracteres numéricos" )
        String cpf,

        List<CriarEnderecoDto> enderecos
) {

}
