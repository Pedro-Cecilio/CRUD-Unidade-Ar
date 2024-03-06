package com.dbserver.crud.domain.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CriarEnderecoDto(
    @NotBlank
    String rua,
    @NotBlank
    String numero,
    @NotBlank
    String bairro,
    @NotBlank
    String cidade,
    @NotBlank
    String estado,

    @Pattern(regexp = "\\b\\d{8}\\b", message = "Cep com formato inválido")
    @NotBlank
    String cep
) {}

