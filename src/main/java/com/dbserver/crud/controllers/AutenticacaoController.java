package com.dbserver.crud.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud.domain.autenticacao.Autenticacao;
import com.dbserver.crud.domain.autenticacao.AutenticacaoService;
import com.dbserver.crud.domain.autenticacao.dto.AutenticacaoRespostaDto;

import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping(value = "/login")
public class AutenticacaoController {

    private AutenticacaoService autenticacaoService;
    public AutenticacaoController(AutenticacaoService autenticacaoService){
        this.autenticacaoService = autenticacaoService;
    }
    
    @PostMapping
    public ResponseEntity<AutenticacaoRespostaDto> login(@RequestBody Autenticacao autenticacao){
        String token = this.autenticacaoService.autenticar(autenticacao);
        return ResponseEntity.ok(new AutenticacaoRespostaDto(token));
    }
}
