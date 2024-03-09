package com.dbserver.crud.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaService;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "/pessoa")
public class PessoaController {
    private PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping("/novo")
    public ResponseEntity<PessoaRespostaDto> criarPessoa(@RequestBody @Validated CriarPessoaDto criarPessoaDto) {
        Pessoa pessoa = this.pessoaService.salvarPessoa(criarPessoaDto);
        PessoaRespostaDto resposta = this.pessoaService.criarPessoaRespostaDto(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<PessoaRespostaDto> atualizarPessoa(@RequestBody @Validated AtualizarDadosPessoaDto atualizarPessoaDto) throws AccessDeniedException{
        Pessoa pessoa = this.pessoaService.pegarPessoaLogada();
        this.pessoaService.atualizarDadosPessoa(atualizarPessoaDto, pessoa);
        PessoaRespostaDto resposta = this.pessoaService.criarPessoaRespostaDto(pessoa);

        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }
}
