package com.dbserver.crud.domain.endereco.dto;

import com.dbserver.crud.domain.endereco.Endereco;

public record EnderecoRespostaDto(

        Long id,
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Boolean principal,
        Long pessoa_id

) {
    public EnderecoRespostaDto(Endereco endereco) {
        this(endereco.getId(), endereco.getRua(), endereco.getNumero(), endereco.getBairro(), endereco.getCidade(),
                endereco.getEstado(), endereco.getCep(), endereco.getPrincipal(), endereco.getPessoa().getId());
    }
}


