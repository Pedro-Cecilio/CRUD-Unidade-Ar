package com.dbserver.crud.domain.endereco.dto;

import com.dbserver.crud.utils.Utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CriarEnderecoDto(
    @NotBlank(message = "Rua deve ser informada")
    String rua,

    @NotBlank
    @Pattern(regexp = Utils.REGEX_NUMERO_ENDERECO, message = "Número com formato inválido")
    String numero,

    @NotBlank(message = "Bairro deve ser informado")
    String bairro,

    @NotBlank(message = "Cidade deve ser informada")
    String cidade,
    
    @NotBlank(message = "Estado deve ser informado")
    String estado,

    @Pattern(regexp = Utils.REGEX_CEP, message = "Cep com formato inválido")
    @NotBlank
    String cep,

    @NotNull
    Boolean principal
) {}

