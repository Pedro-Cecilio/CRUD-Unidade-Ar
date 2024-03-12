package com.dbserver.crud.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaService;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(value = "/pessoa")
public class PessoaController {
    private PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping("/novo")
    public ResponseEntity<PessoaRespostaDto> criarPessoa(@RequestBody @Valid CriarPessoaDto criarPessoaDto) {
        Pessoa pessoa = this.pessoaService.salvarPessoa(criarPessoaDto);
        PessoaRespostaDto resposta = new PessoaRespostaDto(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/atualizar")
    public ResponseEntity<PessoaRespostaDto> atualizarPessoa(
            @RequestBody @Valid AtualizarDadosPessoaDto atualizarPessoaDto) {
        Pessoa pessoa = this.pessoaService.pegarPessoaLogada();
        this.pessoaService.atualizarDadosPessoa(atualizarPessoaDto, pessoa);
        PessoaRespostaDto resposta = new PessoaRespostaDto(pessoa);

        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/todas")
    public ResponseEntity<List<PessoaRespostaDto>> getMethodName(@ParameterObject Pageable pageable) {
        List<PessoaRespostaDto> pessoas = this.pessoaService.pegarTodasPessoas(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pessoas);
    }

}
