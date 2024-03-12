package com.dbserver.crud.domain.endereco.dto;

import com.dbserver.crud.utils.Utils;

import jakarta.validation.constraints.Pattern;

public record AtualizarEnderecoDto(
    String rua,

    @Pattern(regexp = Utils.REGEX_NUMERO_ENDERECO, message = "Número com formato inválido")
    String numero,

    String bairro,

    String cidade,
    
    String estado,

    @Pattern(regexp = Utils.REGEX_CEP, message = "Cep com formato inválido")
    String cep,

    Boolean principal
) {}

