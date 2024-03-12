package com.dbserver.crud.domain.endereco.dto;

import com.dbserver.crud.utils.Utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CriarEnderecoDto(
    @NotBlank
    String rua,

    @NotBlank
    @Pattern(regexp = Utils.REGEX_NUMERO_ENDERECO, message = "Número com formato inválido")
    String numero,

    @NotBlank
    String bairro,

    @NotBlank
    String cidade,
    
    @NotBlank
    String estado,

    @Pattern(regexp = Utils.REGEX_CEP, message = "Cep com formato inválido")
    @NotBlank
    String cep,

    Boolean principal
) {}

