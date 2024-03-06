package com.dbserver.crud.controllers;

import org.springframework.web.bind.annotation.RestController;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "/pessoa")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @PostMapping()
    public ResponseEntity<Pessoa> criarPessoa(@RequestBody CriarPessoaDto criarPessoaDTO) {
        Pessoa pessoa = new Pessoa(criarPessoaDTO);
        this.pessoaRepository.save(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
    }
}
