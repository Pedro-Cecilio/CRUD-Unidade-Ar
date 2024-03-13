package com.dbserver.crud.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.NoSuchElementException;
import com.dbserver.crud.domain.endereco.EnderecoService;
import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoRespostaDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/endereco")
@SecurityRequirement(name = "bearer-key")
public class EnderecoController {
    private EnderecoService enderecoService;
    private PessoaService pessoaService;

    public EnderecoController(EnderecoService enderecoService, PessoaService pessoaService) {
        this.enderecoService = enderecoService;
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public ResponseEntity<EnderecoRespostaDto> cadastrarEndereco(
            @Valid @RequestBody CriarEnderecoDto criarEnderecoDto) {
        Pessoa pessoa = this.pessoaService.pegarPessoaLogada();
        EnderecoRespostaDto resposta = this.enderecoService.criarNovoEndereco(criarEnderecoDto, pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoRespostaDto> atualizarEndereco(
            @Valid @RequestBody AtualizarEnderecoDto atualizarEnderecoDto, @PathVariable("id") String id) {
        try {
            Pessoa pessoa = this.pessoaService.pegarPessoaLogada();
            Long idLong = Long.parseLong(id);
            EnderecoRespostaDto resposta = this.enderecoService.atualizarEndereco(atualizarEnderecoDto, idLong, pessoa);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Endereço não encontrado.");
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping
    public ResponseEntity<List<EnderecoRespostaDto>> pegarTodosEnderecos(@ParameterObject Pageable pageable) {
        Long pessoaId = this.pessoaService.pegarIdDaPessoaLogada();
        List<EnderecoRespostaDto> pessoas = this.enderecoService.pegarTodosEnderecos(pageable, pessoaId);
        return ResponseEntity.status(HttpStatus.OK).body(pessoas);
    }

    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarEndereco(@PathVariable("id") String id) {

        try {
            Long pessoaId = this.pessoaService.pegarIdDaPessoaLogada();
            Long idLong = Long.parseLong(id);
            this.enderecoService.deletarEndereco(idLong, pessoaId);
            return ResponseEntity.status(HttpStatus.OK).body("Endereco deletado com sucesso");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Endereço não encontrado.");
        }
    }

}
