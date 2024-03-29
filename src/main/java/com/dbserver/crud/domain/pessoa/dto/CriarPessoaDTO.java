package com.dbserver.crud.domain.pessoa.dto;

import java.time.LocalDate;
import java.util.List;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.infra.jsonDeserializer.LocalDateDeserializer;
import com.dbserver.crud.utils.Utils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarPessoaDto(
        @Size(min = 5, message = "Login deve ter no mínimo 5 caracteres")
        String login,

        @Size(min = 6, message = "Senha deve conter no mínimo 6 caracteres")
        String senha,

        @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
        String nome,

        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate dataNascimento,

        @NotBlank(message = "Cpf deve ser informado")
        @Pattern(regexp = Utils.REGEX_CPF, message = "Cpf deve conter 11 caracteres numéricos" )
        String cpf,

        List<CriarEnderecoDto> enderecos
) {

}
