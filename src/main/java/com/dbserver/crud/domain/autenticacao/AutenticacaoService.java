package com.dbserver.crud.domain.autenticacao;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.domain.pessoa.PessoaService;
import com.dbserver.crud.infra.security.TokenService;

@Service
public class AutenticacaoService {
    private PessoaRepository pessoaRepository;
    private TokenService tokenService;
    private PasswordEncoder passwordEnconder;

    public AutenticacaoService(PessoaRepository pessoaRepository, TokenService tokenService, PasswordEncoder passwordEnconder) {
        this.pessoaRepository = pessoaRepository;
        this.tokenService = tokenService;
        this.passwordEnconder = passwordEnconder;
    }

    public String autenticar(Autenticacao autenticacao) {
        return pessoaRepository.findByLogin(autenticacao.getLogin())
                .filter(pessoa -> pessoa.compararSenha(autenticacao.getSenha(), this.passwordEnconder))
                .map(pessoa -> tokenService.gerarToken(pessoa))
                .orElseThrow(() -> new BadCredentialsException("Dados de login inválidos"));

    }

    
}
