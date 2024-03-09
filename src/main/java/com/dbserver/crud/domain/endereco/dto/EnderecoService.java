package com.dbserver.crud.domain.endereco.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.pessoa.Pessoa;

@Service
public class EnderecoService {
    private EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }
}
