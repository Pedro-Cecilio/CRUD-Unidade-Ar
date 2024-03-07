package com.dbserver.crud.domain.pessoa;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;
import com.dbserver.crud.infra.security.TokenService;

@Service
public class PessoaService {
    
    private PessoaRepository pessoaRepository;
    private PasswordEncoder passwordEnconder;
    private TokenService tokenService;

    public PessoaService(PessoaRepository pessoaRepository, PasswordEncoder passwordEnconder, TokenService tokenService) {
        this.pessoaRepository = pessoaRepository;
        this.passwordEnconder = passwordEnconder;
        this.tokenService = tokenService;
    }

    public PessoaRespostaDto criarPessoa(CriarPessoaDto criarPessoaDTO){
        
        Pessoa pessoa = new Pessoa(criarPessoaDTO, this.passwordEnconder);
        this.pessoaRepository.save(pessoa);
        return new PessoaRespostaDto(pessoa);
    }

    public boolean validarSenha(Pessoa pessoa, String senha){
        return this.passwordEnconder.matches(senha, pessoa.getSenha());
    }

    public PessoaRespostaDto atualizarDadosPessoa(AtualizarDadosPessoaDto atualizarDadosPessoaDto) throws AccessDeniedException{
        try {
            Pessoa pessoa = this.pegarPessoaPeloId(this.tokenService.pegarIdDaPessoaLogada());
            pessoa.atualizarDados(atualizarDadosPessoaDto, this.passwordEnconder);
            this.pessoaRepository.save(pessoa);
            return new PessoaRespostaDto(pessoa);
        } catch (Exception e) {
            throw new AccessDeniedException("Usuário não está logado na aplicação");
        }
    }

    public Pessoa pegarPessoaPeloId(Long id){
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(id);
        if(pessoa.isPresent()){
            return pessoa.get();
        }
        throw new NoSuchElementException("Usuário não encontrado");
    }
}
