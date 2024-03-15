package com.dbserver.crud.domain.endereco.dto;

import com.dbserver.crud.utils.Utils;

import jakarta.validation.constraints.Pattern;

public record AtualizarEnderecoDto(
    String rua,

    @Pattern(regexp = Utils.REGEX_NUMERO_ENDERECO, message = "Número deve ter somente caracteres numéricos")
    String numero,

    String bairro,

    String cidade,
    
    String estado,

    @Pattern(regexp = Utils.REGEX_CEP, message = "Cep deve ter 8 caracteres numéricos")
    String cep,

    Boolean principal
) {}

