package com.dbserver.crud.domain.endereco.dto;

import jakarta.validation.constraints.Pattern;

public record AtualizarEnderecoDto(
    String rua,

    @Pattern(regexp = "^\\d+$", message = "Número com formato inválido")
    String numero,

    String bairro,

    String cidade,
    
    String estado,

    @Pattern(regexp = "^\\d{8}+$", message = "Cep com formato inválido")
    String cep,

    Boolean principal
) {}

